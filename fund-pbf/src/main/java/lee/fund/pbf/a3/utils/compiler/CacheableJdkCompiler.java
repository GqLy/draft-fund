/*
 * Copyright 2002-2007 the original author or authors.
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
package lee.fund.pbf.a3.utils.compiler;

import lee.fund.pbf.a3.utils.ClassHelper;

import java.io.OutputStream;

/**
 * Cacheable support compiler.
 * 
 * @author xiemalin
 * @since 1.8.6
 */
public abstract class CacheableJdkCompiler implements Compiler {

    protected Compiler compiler;

    public CacheableJdkCompiler(Compiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public Class<?> compile(String className, String code, ClassLoader classLoader, OutputStream os, long timestamp) {
        Class<?> cls = null;
        try {
            cls = ClassHelper.forName(className, classLoader);
        } catch (ClassNotFoundException e) {
            // ignore this exception
        } catch (LinkageError e) {
            // ignore this exception
        }
        if (cls != null) {
            return cls;
        }

        // to check cache
        byte[] bytes = cached(className, timestamp);
        // if has cached and timestamp is not changed will return a not null byte array
        if (bytes != null) {
            LoadableClassLoader loadableClassLoader = new LoadableClassLoader(classLoader);
            loadableClassLoader.defineNewClass(className, bytes, 0, bytes.length);
            
            try {
                return loadableClassLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                // ignore this exception
            }
        }

        cls = compiler.compile(className, code, classLoader, os, timestamp);
        
        cache(className, compiler.loadBytes(className), timestamp);
        return cls;
    }

    protected abstract byte[] cached(String className, long timestamp);

    protected abstract void cache(String className, byte[] bytes, long timestamp);

    protected static class LoadableClassLoader extends ClassLoader {
        protected LoadableClassLoader(ClassLoader parent) {
            super(parent);
        }

        public void defineNewClass(String name, byte[] b, int off, int len) {
            defineClass(name, b, off, len);
        }
    }

}
