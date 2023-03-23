import kotlin.random.Random

fun createDiceGameRandomizer(n: Int): Int {
    // write your code here
    var sumFriend = 0
    var sumUs = 0
    var seed = -1

    do {
        seed++
        sumFriend = 0
        sumUs = 0
        val rngSeed = Random(seed)
        repeat(n) { sumFriend += rngSeed.nextInt(1, 7) }
        repeat(n) { sumUs += rngSeed.nextInt(1, 7) }
    } while (sumFriend >= sumUs)
    return seed
}