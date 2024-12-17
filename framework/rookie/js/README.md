# Rookie for Js

use:
```
@Proto(-10086)
class RequestData {
    @Field("int64")
    id: number;
    @Field("int32")
    age: number;
    @Field("string")
    name: string;
    @Field("float64")
    price: number;
    @Field("float32")
    tail: number;
    @Field("list", "int32")
    list: number[];
    @Field("set", "int64")
    set: number[];
    @Field("map", "int64", "int32")
    map: Record<number, number>;
}
```
than:
```
    const mem = new Memory(new ArrayBuffer(1024));
    const test = new RequestData();
    rookie.serialze(test, mem);
    const deserilaze = rookie.deserilaze(-10086, mem);
    console.log(deserilaze);
   
```
