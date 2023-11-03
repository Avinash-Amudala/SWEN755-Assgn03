# SWEN755-Assgn03

## How to run the code

There are two steps to run the code.
1. Data preparation

    The data is already packed in `/data/Salesdata.zip`, but it needs to be extracted.
    Run `/src/sales/Setup` to unzip the data automatically. There will be messages notifying the completion (or 
   failure) of this operation. If this fails, just manually extract the zip file inside the `\data` folder.

2. Main program

    Run `/src/sales/SalesCalculator` to run the main program. Results will show in the console.

## What does the code do

The program simulates a retail chain that has multiple stores (15 in this simulation). Each store has records of 
every transaction in a quarter, which is three csv files (one file for a month). The program calculates the total 
sales for each store in each month, then finds the store that has the highest sales in each month and the whole quarter.

The class `Setup` finds the zip file, tries to extract the data, and check if it's extracted correctly.

The class `SalesCalculator` is the entry of the program. It establishes a thread pool with 10 threads, a loop walks 
through the data files and provide the files to threads. Each thread receiving the file will create a `SalesReader` 
object to read the file, calculate the total number and store the result in 
a `SalesDataStore` object (which is shared by all the threads). When every file is processed, the class 
`ResultDisplay` will be called to find the sale champions and display all the results in a formal way. 
