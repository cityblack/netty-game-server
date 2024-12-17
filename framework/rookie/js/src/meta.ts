/**
 * Type Mapping between JavaScript and Java
 *
 * | Type              | JavaScript              | Java                |
 * |-------------------|-------------------------|---------------------|
 * | bool              | boolean                 | boolean            |
 * | int8              | number                  | byte               |
 * | int16             | number                  | short              |
 * | int32             | number                  | int                |
 * | int64             | number                  | long               |
 * | float32           | number                  | float              |
 * | float64           | number                  | double             |
 * | string            | string                  | String             |
 * | date              | Date                    | Date               |
 * | array,int32       | number[]                | int[]              |
 * | list,int32        | number[]                | List<Integer>      |
 * | map,int32,string  | Record<number, string>  | Map<Integer,String>|
 * | set,int32         | number[]                | Set<Integer>       |
 */
export type DataType =
  | "bool"
  | "int8"
  | "int16"
  | "int32"
  | "int64"
  | "float32"
  | "float64"
  | "string"
  | "date"
  | "list"
  | "map"
  | "set"
  | "array"
  | "queue"
  | Number;

export interface StructDesc {
  type: DataType;
  valueType?: DataType;
  mapValueType?: DataType;
  writeClass: boolean;
}

export interface FieldInfo extends StructDesc {
  name: string;
}

export const FIELDS_KEY = "$_class";
export const ID_KEY = "$protoId";

export const isBaseType = (type: DataType) => {
  return (
    type === "float32" ||
    type === "float64" ||
    type === "int8" ||
    type === "int16" ||
    type === "int32" ||
    type === "int64" ||
    type === "string" ||
    type === "bool"
  );
};

/**
 *
 * @param type -- object type
 * @param valueType -- map value type. It's required when type is map
 * @returns
 */
export const Field = (
  type: DataType,
  valueType?: DataType,
  mapValueType?: DataType
) => {
  return (target: any, propertyKey: string) => {
    // Ensure the target has a $_class metadata object
    if (!target.constructor[FIELDS_KEY]) {
      target.constructor[FIELDS_KEY] = [];
    }

    // Add or update field metadata
    target.constructor[FIELDS_KEY].push({
      name: propertyKey,
      type,
      valueType,
      mapValueType,
      writeClass: !isBaseType(type),
    } as FieldInfo);
  };
};

export const Proto = (id: number) => {
  return (target: any) => {
    target[ID_KEY] = id;
    target.prototype[ID_KEY] = id;
  };
};
