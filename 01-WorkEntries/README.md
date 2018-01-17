This was part of an online assessment for an entry-level software development position.  Each question had a single text field to enter the code required to solve the problem.

In answering these questions, I wanted to follow OOP guidelines and write code that was above correct, readable, and efficient (in that order).  I also tried to follow the Extreme Programming value of Simplicity, which more or less meant that I aimed to not overdesign my classes by making too many assumptions about the larger context they would exist in.

Interesting things that came out of this test:
- I gained experience using a variety of different Collection structures to solve problems related to a single data set.
- I learned how to implement hashCode methods for my own classes.
- I ran into the first situation I had ever been in where a while(true) loop seemed like the cleanest way to write a block of code.



# The Test

## Premise

The next few questions are designed to be a practical programming exercise that will require answering questions about some JSON-formatted data that represents the history of pieces of work moving through an automated system. Each history entry is comprised of the following:

- id: A unique ID for this history entry
- piece_id: The piece of work being operated on
- status: Number indicating the operation being performed on the piece
- user_id: ID of the user that performed the operation in this entry
- start_time: Time that the piece began being processed in the status
- end_time: Time that the piece finished being processed in the status; the difference between end_time and start_time indicates how long the piece spent in the status

Please consider efficiency in your answers and note any assumptions that you make about the data.

## The questions

### Question 1

How many unique statuses are in the data set?

### Question 2

As mentioned previously, the user_id field in the data set indicates the user that moved the piece into the given status, which is considered one operation; each history entry had one user perform one operation to put it in that status. With that in mind, in descending order, list out the top 5 users by the number of operations performed and the number of operations performed by that user. For example:

- user6: 12345
- user9: 9999
- user1: 7920
- user5: 5801
- user2: 1088

### Question 3

Given that a status ending in 3 represents an error status, what percentage of pieces in this data set end up in an error status at least twice?

### Question 4

On average, how long does a piece spend in status 8951?

### Question 5

What is the most common path for a piece to follow through the system?