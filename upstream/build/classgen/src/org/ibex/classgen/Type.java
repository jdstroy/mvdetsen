package org.ibex.classgen;

import java.util.StringTokenizer;

public class Type {
    public static final Type VOID = new Type("V");
    public static final Type INT = new Type("I");
    public static final Type LONG = new Type("J");
    public static final Type BOOLEAN = new Type("Z");
    public static final Type DOUBLE = new Type("D");
    public static final Type FLOAT = new Type("F");
    public static final Type BYTE = new Type("B");
    public static final Type CHAR = new Type("C");
    public static final Type SHORT = new Type("S");
    
    public static final Type.Object OBJECT = new Type.Object("java.lang.Object");
    public static final Type.Object STRING = new Type.Object("java.lang.String");
    public static final Type.Object STRINGBUFFER = new Type.Object("java.lang.StringBuffer");
    public static final Type.Object INTEGER_OBJECT = new Type.Object("java.lang.Integer");
    public static final Type.Object DOUBLE_OBJECT = new Type.Object("java.lang.Double");
    public static final Type.Object FLOAT_OBJECT = new Type.Object("java.lang.Float");
    
    /** A zero element Type[] array (can be passed as the "args" param when a method takes no arguments */
    public static final Type[] NO_ARGS = new Type[0];
    
    final String descriptor;

    public String humanReadable() {
        if (descriptor.equals("V")) return "void";
        if (descriptor.equals("I")) return "int";
        if (descriptor.equals("J")) return "long";
        if (descriptor.equals("Z")) return "boolean";
        if (descriptor.equals("D")) return "double";
        if (descriptor.equals("F")) return "float";
        if (descriptor.equals("B")) return "byte";
        if (descriptor.equals("C")) return "char";
        if (descriptor.equals("S")) return "short";
        throw new Error("confounded by Type("+descriptor+")");
    }

    protected Type(String descriptor) { this.descriptor = descriptor; }
    
    public static Type fromDescriptor(String d) {
        if (d.equals("V")) return VOID;
        if (d.equals("I")) return INT;
        if (d.equals("J")) return LONG;
        if (d.equals("Z")) return BOOLEAN;
        if (d.equals("D")) return DOUBLE;
        if (d.equals("F")) return FLOAT;
        if (d.equals("B")) return BYTE;
        if (d.equals("C")) return CHAR;
        if (d.equals("S")) return SHORT;
        if (d.endsWith("["))
            return new Type.Array(fromDescriptor(d.substring(0, d.indexOf('['))),
                                  d.length() - d.indexOf('['));
        return new Type.Object(d);
    }
        
    /** Returns the Java descriptor string for this object ("I", or "Ljava/lang/String", "[[J", etc */
    public final String getDescriptor() { return descriptor; }
    public int hashCode() { return descriptor.hashCode(); }
    public boolean equals(java.lang.Object o) { return o instanceof Type && ((Type)o).descriptor.equals(descriptor); }
    
    public String toString() { return getDescriptor(); }
    public Type.Object asObject() { throw new RuntimeException("attempted to use "+this+" as a Type.Object, which it is not"); }
    public Type.Array asArray() { throw new RuntimeException("attempted to use "+this+" as a Type.Array, which it is not"); }
    public Type.Array makeArray() { return new Type.Array(this); }
    public Type.Array makeArray(int dim) { return new Type.Array(this, dim); }
    public boolean isObject() { return false; }
    public boolean isArray() { return false; }

    /** Class representing Object types (any non-primitive type) */
    public static class Object extends Type {
        protected Object(String s) { super(_initHelper(s)); }
        public Type.Object asObject() { return this; }
        public boolean isObject() { return true; }
        public String humanReadable() { return internalForm().replace('/', '.'); }
        public String getShortName() {
            String hr = humanReadable();
            return hr.substring(hr.lastIndexOf('.')+1);
        }

        private static String _initHelper(String s) {
            if (!s.startsWith("L") || !s.endsWith(";")) s = "L" + s.replace('.', '/') + ";";
            return s;
        }

        String[] components() {
            StringTokenizer st = new StringTokenizer(descriptor.substring(1, descriptor.length()-1), "/");
            String[] a = new String[st.countTokens()];
            for(int i=0;st.hasMoreTokens();i++) a[i] = st.nextToken();
            return a;
        }
       
        String internalForm() { return descriptor.substring(1, descriptor.length()-1); }
    }    

    public static class Array extends Object {
        private int dim;
        protected Array(Type t) { super(_initHelper(t, 1)); this.dim = 1; }
        protected Array(Type t, int dim) { super(_initHelper(t, dim)); this.dim = dim; }
        public Type.Array asArray() { return this; }
        public boolean isArray() { return true; }
        public String humanReadable() { 
            String ret = super.internalForm().replace('/', '.');
            for(int i=0; i<dim; i++) ret += "[]";
            return ret;
        }
        String internalForm() { throw new Error("Type.Array does not have an internalForm()"); }
        String[] components() { throw new Error("Type.Array does not have components()"); }
        private static String _initHelper(Type t, int dim) {
            StringBuffer sb = new StringBuffer(t.descriptor.length() + dim);
            for(int i=0;i<dim;i++) sb.append("[");
            sb.append(t.descriptor);
            return sb.toString();
        }
    }

}
