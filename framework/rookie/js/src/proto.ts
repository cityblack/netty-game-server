import { Field, Proto } from "./meta";

@Proto(2002)
export class Item {
  constructor() {
    this.name = "hello";
    this.age = 10;
  }

  @Field("string")
  name: string;
  @Field("int32")
  age: number;
}

@Proto(2001)
export class Test {
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
  @Field("array", 2002)
  items: Item[];
  @Field("list", 2002)
  listItems: Item[];

  constructor() {
    this.name = "hello";
    this.age = 10;
    this.ids = [1, 2, 3];
    this.content = ["a", "b", "c"];
    this.map = {
      1: "a",
      2: "b",
      3: "c",
    };
    this.items = [new Item(), new Item(), new Item(), new Item()];
    this.listItems = [new Item(), new Item(), new Item(), new Item()];
  }
}
