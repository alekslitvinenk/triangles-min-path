# Assumptions

The problem solving algorithm apparently have O(n^2) memory complexity.
https://www.bigocheatsheet.com/
This stem from the fact that we have to memorize each individual path convolution to produce its 
score, compare it with other paths score and give back its reversed path (we use List as an
underlying data structure as it has O(1) complexity for prepending elements).

Given that, to compute result for a pyramid consisting of 50 rows (n = 50), we would have to iterate 
over 2^50 or ~ 1 quadrillion elements, which will cause OutOfMemory execution on most user machines.

In my test runs, using n <= 25 can produce result in a reasonable amount of time.