/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.changedetection.state.mirror;

import com.google.common.collect.Iterables;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.RelativePath;
import org.gradle.api.internal.changedetection.state.DirContentSnapshot;
import org.gradle.api.internal.changedetection.state.FileContentSnapshot;
import org.gradle.api.internal.file.AbstractFileTreeElement;
import org.gradle.api.specs.Spec;
import org.gradle.internal.file.FileType;
import org.gradle.internal.nativeintegration.filesystem.FileSystem;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.io.InputStream;

public class FilteredPhysicalSnapshot implements PhysicalSnapshot {

    private final Spec<FileTreeElement> spec;
    private final PhysicalSnapshot delegate;
    private final FileSystem fileSystem;

    public FilteredPhysicalSnapshot(Spec<FileTreeElement> spec, PhysicalSnapshot delegate, FileSystem fileSystem) {
        this.spec = spec;
        this.delegate = delegate;
        this.fileSystem = fileSystem;
    }

    @Override
    public void accept(final PhysicalSnapshotVisitor visitor) {
        delegate.accept(new PhysicalSnapshotVisitor() {
            private final RelativePathTracker relativePath = new RelativePathTracker();

            @Override
            public boolean preVisitDirectory(String absolutePath, String name) {
                relativePath.enter(name);
                if (relativePath.isRoot() || spec.isSatisfiedBy(new LogicalFileTreeElement(absolutePath, relativePath.getRelativePath(), DirContentSnapshot.INSTANCE, fileSystem))) {
                    visitor.preVisitDirectory(absolutePath, name);
                    return true;
                }
                relativePath.leave();
                return false;
            }

            @Override
            public void visit(String absolutePath, String name, FileContentSnapshot content) {
                relativePath.enter(name);
                if (spec.isSatisfiedBy(new LogicalFileTreeElement(absolutePath, relativePath.getRelativePath(), content, fileSystem))) {
                    visitor.visit(absolutePath, name, content);
                }
                relativePath.leave();
            }

            @Override
            public void postVisitDirectory() {
                relativePath.leave();
                visitor.postVisitDirectory();
            }
        });
    }

    @Override
    public FileType getType() {
        return delegate.getType();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getAbsolutePath() {
        return delegate.getAbsolutePath();
    }

    /**
     * Adapts a logical file snapshot to the {@link FileTreeElement} interface, e.g. to allow
     * passing it to a {@link org.gradle.api.tasks.util.PatternSet} for filtering.
     *
     * The fields on this class are prefixed with _ to avoid users from accidentally referencing them
     * in dynamic Groovy code.
     */
    private static class LogicalFileTreeElement extends AbstractFileTreeElement {
        private final String _absolutePath;
        private final Iterable<String> _relativePathIterable;
        private final FileContentSnapshot _content;
        private final FileSystem _fileSystem;
        private RelativePath _relativePath;
        private File _file;

        public LogicalFileTreeElement(String absolutePath, Iterable<String> relativePathIterable, FileContentSnapshot content, FileSystem fileSystem) {
            super(fileSystem);
            this._absolutePath = absolutePath;
            this._relativePathIterable = relativePathIterable;
            this._content = content;
            this._fileSystem = fileSystem;
        }

        @Override
        public String getDisplayName() {
            return "file '" + getFile() + "'";
        }

        @Override
        public File getFile() {
            if (_file == null) {
                _file = new File(_absolutePath);
            }
            return _file;
        }

        @Override
        public boolean isDirectory() {
            return _content.getType() == FileType.Directory;
        }

        @Override
        public long getLastModified() {
            return getFile().lastModified();
        }

        @Override
        public long getSize() {
            return getFile().length();
        }

        @Override
        public InputStream open() {
            return GFileUtils.openInputStream(getFile());
        }

        @Override
        public RelativePath getRelativePath() {
            if (_relativePath == null) {
                _relativePath = new RelativePath(!isDirectory(), Iterables.toArray(_relativePathIterable, String.class));
            }
            return _relativePath;
        }

        @Override
        public int getMode() {
            return _fileSystem.getUnixMode(getFile());
        }
    }
}
