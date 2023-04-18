Jacob Leavitt, *********

Section 1:

    1. Place data txt files in a folder named 'resources' at the top level directory
    2. Compile the j ava files in the src/ directory (I used IntelliJ IDEA to this automatically)
    3. Run Main.java
    4. input 'exit' to quit the program


Section 2:
    All commands and indexes are fully functional.

Section 3:
    The main program is a bit messy, but the logic is done to parse the query statement.
    Options:
        Command is to create index, then create them unless already made.
        Command length is > 45, so must be a query.
            Pull the char at index 44, this will either be an '=', '!', or '<'
            Apply proper query based on key.

    The HashIndex and ArrayClasses are basically empty, only a wrapper for the indexes themselves.

    The IndexPair class is to accommodate making both indexes in one pass of the files. It's
    only used once.

    The Pair class contains file and offset values for the indexes. Very helpful

    The FileSupport class does all the heavy lifting. It handles all I/O. I will admit that
    querying a specific record will read the entire file anyway, but only that one record is accessed
    in the array. Files.readAllBytes() is an amazing method that removes so much of the I/O headache.