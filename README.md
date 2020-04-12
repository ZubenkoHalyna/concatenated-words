**The Concatenated words challenge**

We have provided a file called “wordsforproblem.txt” which contains a sorted list of approximately 173,000 words. The words are listed one word per line, do not contain spaces, and are all lowercase. Your task is to write a program that reads the file and provides the following:

the longest concatenated word (that is, the longest word that is comprised entirely of shorter words in the file)
the 2nd longest concatenated word
the total count of all the concatenated words in the file
For example, if the file contained:

cat
cats
catsdogcats
dog
dogcatsdog
hippopotamuses
rat
ratcatdogcat

The longest concatenated word would be “ratcatdogcat”; with 12 characters. ‘hippopotamuses’ isa longer word, however it is not comprised entirely of shorter words in the list.

The 2nd longest concatenated word is ‘catsdogcats’ with 11 characters.

The total number of concatenated words is 3. Note that ‘cats’ is not a concatenated word because there is no word ‘s’ in the list.