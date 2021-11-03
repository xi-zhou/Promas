/**
 * Copyright (c) 2006-2019 JEP AUTHORS.
 *
 * This file is licensed under the the zlib/libpng License.
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any
 * damages arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any
 * purpose, including commercial applications, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 
 *     1. The origin of this software must not be misrepresented; you
 *     must not claim that you wrote the original software. If you use
 *     this software in a product, an acknowledgment in the product
 *     documentation would be appreciated but is not required.
 * 
 *     2. Altered source versions must be plainly marked as such, and
 *     must not be misrepresented as being the original software.
 * 
 *     3. This notice may not be removed or altered from any source
 *     distribution.
 */
package jep;

/**
 * Utility functions
 * 
 * @author Mike Johnson
 */
public final class Util {

    /*
     * TODO this class should eventually not be needed, convert its
     * functionality to native code
     */

    // these must be the same as util.h
    protected static final int JBOOLEAN_ID = 0;

    protected static final int JINT_ID = 1;

    protected static final int JLONG_ID = 2;

    protected static final int JOBJECT_ID = 3;

    protected static final int JSTRING_ID = 4;

    protected static final int JVOID_ID = 5;

    protected static final int JDOUBLE_ID = 6;

    protected static final int JSHORT_ID = 7;

    protected static final int JFLOAT_ID = 8;

    protected static final int JARRAY_ID = 9;

    protected static final int JCHAR_ID = 10;

    protected static final int JBYTE_ID = 11;

    protected static final int JCLASS_ID = 12;

    private Util() {
    }

    /**
     * <pre>
     * 
     * <b>Internal use only</b>
     * 
     * Does the same thing as util.c::get_jtype, but it's easier more
     * stable to do this in java when able.
     * 
     * </pre>
     * 
     * @param obj
     *            an <code>Object</code> value
     * @return an <code>int</code> one of the type _ID constants
     */
    protected static final int getTypeId(Object obj) {
        if (obj == null) {
            return -1;
        }

        if (obj instanceof Integer) {
            return JINT_ID;
        }

        if (obj instanceof Short) {
            return JSHORT_ID;
        }

        if (obj instanceof Double) {
            return JDOUBLE_ID;
        }

        if (obj instanceof Float) {
            return JFLOAT_ID;
        }

        if (obj instanceof Boolean) {
            return JBOOLEAN_ID;
        }

        if (obj instanceof Long) {
            return JLONG_ID;
        }

        if (obj instanceof String) {
            return JSTRING_ID;
        }

        if (obj instanceof Void) {
            return JVOID_ID;
        }

        if (obj instanceof Character) {
            return JCHAR_ID;
        }

        if (obj instanceof Byte) {
            return JBYTE_ID;
        }

        if (obj instanceof Class) {
            return JCLASS_ID;
        }

        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return JARRAY_ID;
        }

        return JOBJECT_ID;
    }

    /**
     * <pre>
     * 
     * <b>Internal use only</b>
     * 
     * Same as <code>getTypeId(Object)</code> but for Class. This is
     * useful for determining the _ID for things like
     * method.getReturnType.
     * 
     * </pre>
     * 
     * @param clazz
     *            an <code>Object</code> value
     * @return an <code>int</code> one of the type _ID constants
     */
    protected static final int getTypeId(Class<?> clazz) {
        if (clazz == null) {
            return -1;
        }

        if (clazz.isAssignableFrom(Integer.class)) {
            return JINT_ID;
        }

        if (clazz.isAssignableFrom(Short.class)) {
            return JSHORT_ID;
        }

        if (clazz.isAssignableFrom(Double.class)) {
            return JDOUBLE_ID;
        }

        if (clazz.isAssignableFrom(Float.class)) {
            return JFLOAT_ID;
        }

        if (clazz.isAssignableFrom(Boolean.class)) {
            return JBOOLEAN_ID;
        }

        if (clazz.isAssignableFrom(Long.class)) {
            return JLONG_ID;
        }

        if (clazz.isAssignableFrom(String.class)) {
            return JSTRING_ID;
        }

        if (clazz.isAssignableFrom(Void.class)) {
            return JVOID_ID;
        }

        if (clazz.isAssignableFrom(Character.class)) {
            return JCHAR_ID;
        }

        if (clazz.isAssignableFrom(Byte.class)) {
            return JBYTE_ID;
        }

        if (clazz.isAssignableFrom(Class.class)) {
            return JCLASS_ID;
        }

        if (clazz.isArray()) {
            return JARRAY_ID;
        }

        return JOBJECT_ID;
    }
}
