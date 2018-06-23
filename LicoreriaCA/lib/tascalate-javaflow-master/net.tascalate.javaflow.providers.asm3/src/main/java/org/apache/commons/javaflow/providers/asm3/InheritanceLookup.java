package org.apache.commons.javaflow.providers.asm3;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.javaflow.spi.ResourceLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A class that computes the common super class of two classes without
 * actually loading them with a ClassLoader.
 * 
 * @author Eric Bruneton
 * @author vsilaev
 */
public class InheritanceLookup {
    
    private final ResourceLoader loader;
    private final Map<Key, String> lookupCache = new HashMap<Key, String>();
    
    public InheritanceLookup(ResourceLoader loader) {
        this.loader = loader;
    }
    
    public Type getCommonSuperType(Type type1, Type type2) {
        return Type.getObjectType(getCommonSuperClass(type1.getInternalName(), type2.getInternalName()));
    }
    
    public String getCommonSuperClass(String type1, String type2) {
        Key key = new Key(type1, type2);
        String result;
        synchronized (lookupCache) {
            result = lookupCache.get(key);
            if (null == result) {
                result = calculateCommonSuperClass(type1, type2);
                lookupCache.put(key, result);
            }
        }
        return result;
    }

    protected String calculateCommonSuperClass(final String type1, final String type2) {
        try {
            ClassReader info1 = typeInfo(type1);
            ClassReader info2 = typeInfo(type2);
            if ((info1.getAccess() & Opcodes.ACC_INTERFACE) != 0) {
                if (typeImplements(type2, info2, type1)) {
                    return type1;
                }
                if ((info2.getAccess() & Opcodes.ACC_INTERFACE) != 0) {
                    if (typeImplements(type1, info1, type2)) {
                        return type2;
                    }
                }
                return "java/lang/Object";
            }
            if ((info2.getAccess() & Opcodes.ACC_INTERFACE) != 0) {
                if (typeImplements(type1, info1, type2)) {
                    return type2;
                } else {
                    return "java/lang/Object";
                }
            }
            StringBuilder b1 = typeAncestors(type1, info1);
            StringBuilder b2 = typeAncestors(type2, info2);
            String result = "java/lang/Object";
            int end1 = b1.length();
            int end2 = b2.length();
            while (true) {
                int start1 = b1.lastIndexOf(";", end1 - 1);
                int start2 = b2.lastIndexOf(";", end2 - 1);
                if (start1 != -1 && start2 != -1
                        && end1 - start1 == end2 - start2) {
                    String p1 = b1.substring(start1 + 1, end1);
                    String p2 = b2.substring(start2 + 1, end2);
                    if (p1.equals(p2)) {
                        result = p1;
                        end1 = start1;
                        end2 = start2;
                    } else {
                        return result;
                    }
                } else {
                    return result;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Returns the internal names of the ancestor classes of the given type.
     * 
     * @param type
     *            the internal name of a class or interface.
     * @param info
     *            the ClassReader corresponding to 'type'.
     * @return a StringBuilder containing the ancestor classes of 'type',
     *         separated by ';'. The returned string has the following format:
     *         ";type1;type2 ... ;typeN", where type1 is 'type', and typeN is a
     *         direct subclass of Object. If 'type' is Object, the returned
     *         string is empty.
     * @throws IOException
     *             if the bytecode of 'type' or of some of its ancestor class
     *             cannot be loaded.
     */
    protected StringBuilder typeAncestors(String type, ClassReader info) throws IOException {
        StringBuilder b = new StringBuilder();
        while (!"java/lang/Object".equals(type)) {
            b.append(';').append(type);
            type = info.getSuperName();
            info = typeInfo(type);
        }
        return b;
    }

    /**
     * Returns true if the given type implements the given interface.
     * 
     * @param type
     *            the internal name of a class or interface.
     * @param info
     *            the ClassReader corresponding to 'type'.
     * @param itf
     *            the internal name of a interface.
     * @return true if 'type' implements directly or indirectly 'itf'
     * @throws IOException
     *             if the bytecode of 'type' or of some of its ancestor class
     *             cannot be loaded.
     */
    protected boolean typeImplements(String type, ClassReader info, String itf) throws IOException {
        while (!"java/lang/Object".equals(type)) {
            String[] itfs = info.getInterfaces();
            for (int i = 0; i < itfs.length; ++i) {
                if (itfs[i].equals(itf)) {
                    return true;
                }
            }
            for (int i = 0; i < itfs.length; ++i) {
                if (typeImplements(itfs[i], typeInfo(itfs[i]), itf)) {
                    return true;
                }
            }
            type = info.getSuperName();
            info = typeInfo(type);
        }
        return false;
    }

    /**
     * Returns a ClassReader corresponding to the given class or interface.
     * 
     * @param type
     *            the internal name of a class or interface.
     * @return the ClassReader corresponding to 'type'.
     * @throws IOException
     *             if the bytecode of 'type' cannot be loaded.
     */
    protected ClassReader typeInfo(String type) throws IOException {
        InputStream is = loader.getResourceAsStream(type + ".class");
        try {
            return new ClassReader(is);
        } finally {
            is.close();
        }
    }
    
    static class Key extends AmbivalentDuoKey<String> {
        Key(String a, String b) {
            super(a, b);
        }
    }
    
    static class AmbivalentDuoKey<T> {
        private final T a;
        private final T b;
        AmbivalentDuoKey(T a, T b) {
            this.a = a;
            this.b = b;
        }
        
        @Override 
        public int hashCode() {
            int hA = null == a ? 0 : a.hashCode();
            int hB = null == b ? 0 : b.hashCode();
            return Math.min(hA, hB) * 37 + Math.max(hA, hB);
        }
        
        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other.getClass() != this.getClass()) {
                return false;
            }
            @SuppressWarnings("unchecked")
            AmbivalentDuoKey<T> that = (AmbivalentDuoKey<T>)other;
            return same(this.a, that.a) && same(this.b, that.b) ||
                   same(this.a, that.b) && same(this.b, that.a);  
        }
        
        private static <T> boolean same(T a, T b) {
            return a == null ? b == null : a.equals(b);
        }
    }
}
