import { Field, Proto } from "./rookies";

class Item implements Proto {
  constructor() {
    this.name = "hello";
    this.age = 10;
  }

  @Field("string")
  name: string;
  @Field("int32")
  age: number;
  get protoId() {
    return 2;
  }
}

export class Test implements Proto {
  @Field("string")
  name: string;
  @Field("int32")
  age: number;
  @Field("list", "int32")
  ids: number[];
  @Field("array", "string")
  content: string[];
  @Field("map", "int64", "string")
  map: Record<number, string>;
  @Field("array", 2)
  items: Item[];
  @Field("list", 2)
  listItems: Item[];
  get protoId() {
    return 1;
  }
}

const test = new Test();
test.name = "hello";
test.age = 10;
test.ids = [1, 2, 3];
test.content = ["a", "b", "c"];
test.map = {
  1: "a",
  2: "b",
  3: "c",
};
test.items = [new Item(), new Item(), new Item(), new Item()];
test.listItems = [new Item(), new Item(), new Item(), new Item()];


