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
package lee.fund.pbf.test.lib;

import com.google.protobuf.WireFormat;

/**
 * Field type for Protobuf.
 *
 * @author xiemalin
 * @since 1.0.0
 */
public enum FieldType {

    /**
     * types defined in .proto file.
     */
    DOUBLE("Double", "double", "WIRETYPE_FIXED64", ".doubleValue()", WireFormat.FieldType.DOUBLE, "0d", 1),

    /**
     * The float.
     */
    FLOAT("Float", "float", "WIRETYPE_FIXED32", ".floatValue()", WireFormat.FieldType.FLOAT, "0f", 2),

    /**
     * The IN t64.
     */
    INT64("Long", "int64", "WIRETYPE_VARINT", ".longValue()", WireFormat.FieldType.INT64, "0L", 3),

    /**
     * The UIN t64.
     */
    UINT64("Long", "uInt64", "WIRETYPE_VARINT", ".longValue()", WireFormat.FieldType.UINT64, "0L", 4),

    /**
     * The IN t32.
     */
    INT32("Integer", "int32", "WIRETYPE_VARINT", ".intValue()", WireFormat.FieldType.INT32, "0", 5),

    /**
     * The FIXE d64.
     */
    FIXED64("Long", "fixed64", "WIRETYPE_FIXED64", ".longValue()", WireFormat.FieldType.FIXED64, "0L", 6),

    /**
     * The FIXE d32.
     */
    FIXED32("Integer", "fixed32", "WIRETYPE_FIXED32", ".intValue()", WireFormat.FieldType.FIXED32, "0", 7),

    /**
     * The bool.
     */
    BOOL("Boolean", "bool", "WIRETYPE_VARINT", ".booleanValue()", WireFormat.FieldType.BOOL, "false", 8),

    /**
     * The string.
     */
    STRING("String", "string", "WIRETYPE_LENGTH_DELIMITED", "", WireFormat.FieldType.STRING, "\"\"", 9),

    /**
     * The bytes.
     */
    BYTES("byte[]", "bytes", "WIRETYPE_LENGTH_DELIMITED", "", WireFormat.FieldType.BYTES, "new byte[0]", 10),

    /**
     * The UIN t32.
     */
    UINT32("Integer", "uInt32", "WIRETYPE_VARINT", ".intValue()", WireFormat.FieldType.UINT32, "0", 11),

    /**
     * The SFIXE d32.
     */
    SFIXED32("Integer", "sFixed32", "WIRETYPE_FIXED32", ".intValue()", WireFormat.FieldType.SFIXED32, "0", 12),

    /**
     * The SFIXE d64.
     */
    SFIXED64("Long", "sFixed64", "WIRETYPE_FIXED64", ".longValue()", WireFormat.FieldType.SFIXED64, "0L", 13),

    /**
     * The SIN t32.
     */
    SINT32("Integer", "sInt32", "WIRETYPE_VARINT", ".intValue()", WireFormat.FieldType.SINT32, "0", 14),

    /**
     * The SIN t64.
     */
    SINT64("Long", "sInt64", "WIRETYPE_VARINT", ".longValue()", WireFormat.FieldType.SINT64, "0L", 15),

    /**
     * The object.
     */
    OBJECT("Object", "object", "WIRETYPE_LENGTH_DELIMITED", "", WireFormat.FieldType.MESSAGE, null, 16),

    /**
     * The enum.
     */
    ENUM("Enum", "enum", "WIRETYPE_VARINT", ".ordinal()", WireFormat.FieldType.ENUM, null, 17),

    /**
     * The default.
     */
    DEFAULT("", "", "", "", WireFormat.FieldType.MESSAGE, null, 0);

    /**
     * java original type.
     */
    private final String javaType;

    /**
     * protobuf type.
     */
    private final String type;

    /**
     * protobuf wire format type.
     */
    private final String wireFormat;

    /**
     * to primitive type.
     */
    private final String toPrimitiveType;

    /**
     * internal field type.
     */
    private WireFormat.FieldType internalFieldType;

    /**
     * default value.
     */
    private String defaultValue;

    /**
     * kind
     */
    private int kind;

    /**
     * Gets the suffix.
     *
     * @return the suffix
     */
    public int getKind() {
        return kind;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the internal field type.
     *
     * @return the internal field type
     */
    public WireFormat.FieldType getInternalFieldType() {
        return internalFieldType;
    }

    /**
     * Sets the internal field type.
     *
     * @param internalFieldType the new internal field type
     */
    public void setInternalFieldType(WireFormat.FieldType internalFieldType) {
        this.internalFieldType = internalFieldType;
    }

    /**
     * Gets the to primitive type.
     *
     * @return the to primitive type
     */
    public String getToPrimitiveType() {
        return toPrimitiveType;
    }

    /**
     * Gets the protobuf wire format type.
     *
     * @return the protobuf wire format type
     */
    public String getWireFormat() {
        return wireFormat;
    }

    /**
     * Gets the protobuf type.
     *
     * @return the protobuf type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the java original type.
     *
     * @return the java original type
     */
    public String getJavaType() {
        if (this == FieldType.ENUM) {
            return Enum.class.getName();
        }
        return javaType;
    }

    /**
     * Constructor method.
     *
     * @param javaType          java original type
     * @param type              protobuf type
     * @param wireFormat        protobuf wire format type
     * @param toPrimitiveType   the to primitive type
     * @param internalFieldType the internal field type
     * @param defaultValue      the default value
     * @param kind              the kind
     */
    FieldType(String javaType, String type, String wireFormat, String toPrimitiveType,
              WireFormat.FieldType internalFieldType, String defaultValue, int kind) {
        this.javaType = javaType;
        this.type = type;
        this.wireFormat = wireFormat;
        this.toPrimitiveType = toPrimitiveType;
        this.internalFieldType = internalFieldType;
        this.defaultValue = defaultValue;
        this.kind = kind;
    }
}
