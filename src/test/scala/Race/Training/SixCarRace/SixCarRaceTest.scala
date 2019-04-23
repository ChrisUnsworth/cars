package Race.Training.SixCarRace

import org.scalatest.{FunSuite, Matchers}

class SixCarRaceTest extends FunSuite with Matchers {

  test ("permutations"){
    val result = SixCarRace.permutations(List(1, 2, 3)).toIterator

    result.hasNext shouldBe true
    result.next() shouldBe List(1, 2, 3)
    result.hasNext shouldBe true
    result.next() shouldBe List(2, 3, 1)
    result.hasNext shouldBe true
    result.next() shouldBe List(3, 1, 2)
  }


  test ("points") {
    val points = SixCarRace.points(6)

    points.size shouldBe 6
    points.head shouldBe 10000
    points(1) shouldBe 30314
    points(2) shouldBe 57995
    points(3) shouldBe 91896
    points(4) shouldBe 131326
    points(5) shouldBe 175809

  }
}
