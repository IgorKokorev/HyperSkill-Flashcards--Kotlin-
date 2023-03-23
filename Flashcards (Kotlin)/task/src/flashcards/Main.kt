package flashcards

import kotlin.random.Random
import java.io.File

// Flashcard with term, definition and error count
data class Flashcard( var term: String, var definition: String, var errNum: Int)

// All the flashcards
val flCards : MutableList<Flashcard> = mutableListOf()
const val SEPARATOR = "\t\t\t" // Separates flashcard fields in files
val consoleLog : MutableList<String> = mutableListOf() // saves all the input/output lines
var logEndOfLine = true

fun main(args: Array<String>) {
    val importFN = checkArguments(args, "-import")
    if (importFN.isNotEmpty()) import(importFN)

    do {
        fcPrintln("Input the action (add, remove, import, export, ask, exit):")
        val command = fcRead().lowercase()
        when (command) {
            "exit" -> break
            "add" -> add()
            "remove" -> remove()
            "ask" -> ask()
            "import" -> import()
            "export" -> export()
            "log" -> saveLog()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
         }
    } while (true)

    val exportFN = checkArguments(args, "-export")
    if (exportFN.isNotEmpty()) export(exportFN)

    fcPrintln("Bye bye!")
}

fun checkArguments(args: Array<String>, arg: String): String {
    for (i in 0..args.lastIndex-1) {
        if (args[i].equals(arg)) return args[i+1]  // Nr of arguments needs check
    }
    return ""
}

fun hardestCard() {
    val errTerms : MutableList<String> = mutableListOf()
    var maxErrNum = 0

    for (flCard in flCards) {
        if (flCard.errNum > maxErrNum) {
            errTerms.clear()
            maxErrNum = flCard.errNum
            errTerms.add(flCard.term)
        } else if (flCard.errNum == maxErrNum && maxErrNum >0) {
            errTerms.add(flCard.term)
        }
    }
    when(errTerms.size) {
        0 -> fcPrintln("There are no cards with errors.")
        1 -> fcPrintln("The hardest card is \"${errTerms[0]}\". You have $maxErrNum errors answering it.")
        else -> {
            fcPrint("The hardest cards are \"${errTerms[0]}\"")
            for (i in 1..errTerms.lastIndex) fcPrint(", \"${errTerms[i]}\"")
            fcPrintln(". You have $maxErrNum errors answering them.")
        }
    }
}

fun resetStats() {
    for (flCard in flCards) flCard.errNum = 0
    fcPrintln("Card statistics have been reset.")
}

// saves log to a file
fun saveLog() {
    fcPrintln("File name:")
    val fileName = fcRead()
    val logFile = File(fileName)

    logFile.writeText("")
    for (str in consoleLog) logFile.appendText(str + "\n")
    fcPrintln("The log has been saved.")
}

// prints a line and write it to log, eol - do we need to print \n
fun fcPrint( str: String) {
    if (logEndOfLine) consoleLog.add(str)
    else consoleLog[consoleLog.lastIndex] = consoleLog.last() + str
    print(str)
    logEndOfLine = false
}

fun fcPrintln( str: String ) {
    fcPrint(str)
    println("")
    logEndOfLine = true
}

// reads a string and write it to log
fun fcRead() : String {
    val str = readln()
    if (logEndOfLine) consoleLog.add(str)
    else {
        consoleLog[consoleLog.lastIndex] = consoleLog.last() + str
        logEndOfLine = true
    }
    return str
}

fun import() {
    fcPrintln("File name:")
    val fileName = fcRead()
    import(fileName)
}

fun import(fileName: String) {
    val flCardsFile = File(fileName)

    if (flCardsFile.exists()) { // checks if file exists
        val lines = flCardsFile.readLines()
        for (i in 1..lines.lastIndex) {
            val splitLine = lines[i].split(SEPARATOR)
            val newFlCard = Flashcard(splitLine[0], splitLine[1], splitLine[2].toInt())
            val ind = indTerm(newFlCard.term)
            if (ind < 0 ) flCards.add(newFlCard)
            else flCards[ind].definition = newFlCard.definition
        }
        fcPrintln("${lines.size-1} cards have been loaded.")
    } else fcPrintln("File not found.")
}

fun export() {
    fcPrintln("File name:")
    val fileName = fcRead()
    export(fileName)
}

fun export(fileName: String) {
    val flCardsFile = File(fileName)

    flCardsFile.writeText("==FlashCards==\n")
    for (flCard in flCards) {
        flCardsFile.appendText("${flCard.term}$SEPARATOR${flCard.definition}$SEPARATOR${flCard.errNum}\n")
    }
    fcPrintln("${flCards.size} cards have been saved.")
}

fun ask() {
    //reading the total number of cards
    var numCards : Int
    do {
        fcPrintln("How many times to ask?")
        numCards = fcRead().toInt()
    } while (numCards <= 0)

    val rng = Random.Default

    repeat(numCards) {
        val i = rng.nextInt(flCards.size)
        fcPrintln("Print the definition of \"${flCards[i].term}\":")
        val answer = fcRead()
        if (answer.equals(flCards[i].definition)) {
            fcPrintln("Correct!")
        } else {
            fcPrint("Wrong. The right answer is \"${flCards[i].definition}\"")
            val ind = indDef(answer)
            if (ind < 0) fcPrintln(".")
            else fcPrintln(", but your definition is correct for \"${flCards[ind].term}\".")
            flCards[i].errNum++
        }
    }

}

fun add() {
    // reading card
    fcPrintln("The card:")
    val term = fcRead()
    var i = indTerm(term)
    if ( i >= 0 ) {
        fcPrintln("The card \"$term\" already exists.")
        return
    }

    fcPrintln("The definition of the card:")
    val definition = readln()
    i = indDef(definition)
    if ( i >= 0 ) {
        fcPrintln("The definition \"$definition\" already exists.")
        return
    }

    flCards.add(Flashcard(term, definition, 0))
    fcPrintln("The pair (\"$term\":\"$definition\") has been added.")
}

fun remove() {
    fcPrintln("Which card?")
    val term = fcRead()

    val ind = indTerm(term)
    if (ind < 0) {
        fcPrintln("Can't remove \"$term\": there is no such card.")
    } else {
        flCards.removeAt(ind)
        fcPrintln("The card has been removed.")
    }
}

fun indTerm( term: String ) : Int {
    for (i in 0..flCards.lastIndex) {
        if (flCards[i].term.equals(term)) return i
    }
    return -1
}

fun indDef( def: String ) : Int {
    for (i in 0.. flCards.lastIndex) {
        if (flCards[i].definition.equals(def)) return i
    }
    return -1
}
