import { RequestData } from "./proto";
import Memory from "./../memonry";
import Rookie from "../rookies";
import { expect, test } from "@jest/globals";
import * as proto from "./proto";

test("serialize", () => {
  const rookie = new Rookie();
  Object.values(proto).forEach((value) => {
    if (typeof value === "function") {
      rookie.register(value);
    }
  });

  const mem = new Memory(new ArrayBuffer(1024));
  const test = new RequestData();
  test.id = 100;
  test.name = "hello";
  test.age = 10;
  test.price = 10.1;
  test.tail = 10;
  test.list = [1, 2, 3];
  test.set = [1, 2, 3];
  test.map = {
    1: 1,
    2: 2,
    3: 3,
  };
  rookie.serialze(test, mem);
  const deserialize = rookie.deserilaze(mem);
  console.log(deserialize);
  expect(deserialize).toEqual(test);
});
