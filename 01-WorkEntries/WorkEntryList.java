/**
 * WorkEntryList.java begin
 * Written in Java
 */ 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Arrays;

/**
 * A list of WorkEntries, capable of doing some analysis on the data
 */
public class WorkEntryList
{  
  
  /******************************************
   *          Inner classes - start
   ******************************************/
    
  /*
   * A temporary record of the number of WorkEntries associated with a specific user
   */
  private static class UserEntries implements Comparable<UserEntries>
  {    
    public long user_id;
    public int entries;
    
    /* constructor */
    public UserEntries(long user_id, int entries)
    {
      this.user_id = user_id;
      this.entries = entries;
    }
    
    /* desired result: more entries are placed BEFORE lower entries (ie - 8 comes before 3) */
    public int compareTo(UserEntries other)
    {
      if(this.entries > other.entries)
      {
        return -1;
      }
      else if(this.entries < other.entries)
      {
        return 1;
      }
      // tie-breaker: show lower user id's first
      else if(this.user_id < other.user_id)
      {
        return -1;
      }
      else
      {
        return 1;
      }
    }    
  }
  
  
  /**
   * A piece of work in the system may undergo various statuses over time.
   * The StatusPath object represents a piece of work's progression through these statusues.
   */
  private class StatusPath implements Comparable<StatusPath>
  {
    // sorted, O(log(n)) insertion/retrieval
    private TreeMap<Long, Long> path = new TreeMap<Long, Long>();
    
    /* constructor, empty list */
    public StatusPath()
    {   
    }
    
    /**
     * adds a status and corresponding time
     */
    public Long put(long start_time, long status)
    {
      return path.put(start_time, status);
    }
    
    /**
     * Returns a chronological list of this path's statuses
     */
    public long[] getPathAsArray()
    {
      long[] statusList = new long[path.size()];
      
      int i=0;
      for(long key : path.keySet())
      {
        statusList[i] = path.get(key);
        i++;
      }
      
      return statusList;
    }
    
    public String toString()
    {
      StringBuilder result = new StringBuilder("");
      String fencepost = "";
      for(long key : path.keySet())
      { 
        result.append("" + fencepost + path.get(key));
        fencepost = " ";        
      }
      return result.toString();
    }
    
    /* two StatusPaths are equal if they have the exact same order of statuses */
    
    public int compareTo(StatusPath other)
    {
      Iterator<Long> myIterator = this.path.keySet().iterator();
      Iterator<Long> yourIterator = other.path.keySet().iterator();
      
      while(true)
      {
        // if reached the end of both paths simultaneously, they're a match
        if(!(myIterator.hasNext()) && !(yourIterator.hasNext()))
        {
          return 0;
        }
        
        // if one path ended before the other, not a match
        if(myIterator.hasNext() != yourIterator.hasNext())
        {
          return -1;
        }
        
        // if status not the same at this step, not a match
        long myStatus = this.path.get(myIterator.next());
        long yourStatus = other.path.get(yourIterator.next());      
        if(myStatus != yourStatus)
        {
          return -1;
        }
      }
    }
    
    @Override
    public boolean equals(Object obj)
    {   
      if(obj == null || !(obj instanceof StatusPath))
      {
        return false;
      }

      final StatusPath other = (StatusPath)obj;  
      return this.compareTo(other) == 0;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 37;
      int result = 13;
      
      for(long key : path.keySet())
      {
        long value = path.get(key);
        int modifiedValue = (int)(value ^ (value >>> 32));
        result = prime * result + modifiedValue;
      }

      return result;
    }
  }

  
  /*****************************************************
   *               Inner classes - end
   * WorkEntryList member variables and methods - start
   *****************************************************/
  
  // average O(1) insertion/retrieval, no duplicates
  private HashSet<WorkEntry> entryList = new HashSet<WorkEntry>(); 
  
  /* constructor */
  public WorkEntryList()
  {
  }
  
      
  /**
   * Reads in data from JSON file to create list.
   * Assumption: file is formatted as follows...    
   *   [
   *   {
   *     "id": 333831567, 
   *     "piece_id": 25395616, 
   *     "status": 10800, 
   *     "user_id": 911, 
   *     "start_time": 1490989764, 
   *     "end_time": 1491001113
   *   },
   *   {
   *     "id": 331198176, 
   *     "piece_id": 25221582, 
   *     "status": 8951, 
   *     "user_id": 393, 
   *     "start_time": 1489499641, 
   *     "end_time": null
   *   }
   *   ]   
   */
  public void createListFromFile(String filename)
  {
    File file = new File(filename);
    Scanner scan = null;
  
    // open the file
    try
    {
      scan = new Scanner(file);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    
    // get past header
    scan.nextLine(); // [
    
    // each loop reads and stores a wave
    while(scan.hasNextLine() && scan.hasNext("\\{"))
    {      
      // trim {
      scan.nextLine();
      
      // get the data
      long a = parseDataFromLine(scan.nextLine());
      long b = parseDataFromLine(scan.nextLine());
      long c = parseDataFromLine(scan.nextLine());
      long d = parseDataFromLine(scan.nextLine());
      long e = parseDataFromLine(scan.nextLine());
      long f = parseDataFromLine(scan.nextLine());
      
      // trim }
      scan.nextLine();
      
      // make entry from data
      WorkEntry entry = new WorkEntry(a,b,c,d,e,f);
      
      // put the entry in the list
      entryList.add(entry);
    }
    
    scan.close();
  }
    
  
  /**
   * Given a String containing a key-value pair, return the value.
   * If the value is null, returns -1 as a sentinel.
   * 
   * Assumption: line is formatted correctly as either...   
   *    "end_time": 1489499641,   
   * ...or...  
   *    "end_time": null,  
   */
  private long parseDataFromLine(String line)
  {
    Scanner scan = new Scanner(line);
    
    // get rid of first token, we don't need it
    scan.next();
    
    // set delimiters to punctuation and whitespace (to remove potential commas)
    scan.useDelimiter("[\\p{javaWhitespace}\\p{Punct}]+");
    
    long data;    
    if(scan.hasNextLong())
    {
      data = scan.nextLong();
    }
    else
    {
      data = -1;
    }  
    
    scan.close();
    return data;
  }    
    
    
  /**
   * Returns the amount of unique status values in the list's entries.
   * I'm interpreting "unique values" as meaning "don't include the repeats",
   * as opposed to "only include values that are never repeated".
   */
  public long getNumberOfUniqueStatuses()
  {    
    // average O(1) insertion/retrieval, no duplicates
    HashSet<Long> statusList = new HashSet<Long>();
    
    for (WorkEntry entry : entryList)
    {
      statusList.add(entry.getStatus());
    }
    
    return statusList.size();        
  }
    

  
  /**
   * Outputs a list, in descending order, of the X users assigned to the most entries.
   */
  public void printMostActiveUsers(int maxUsersToPrint)
  {
    if(maxUsersToPrint < 0)
    {
      throw new IllegalArgumentException("WorkEntryList.printMostActiveUsers() does not accept negative arguments.");
    }
    
    // create list of all users and how many entries they were listed in
    // average O(1) insertion/retrieval
    HashMap<Long, Integer> allUsers = new HashMap<Long, Integer>();
    for (WorkEntry entry : entryList)
    {        
      if(entry.getUserId() > -1)
      {      
        Integer numEntries = allUsers.get(entry.getUserId());      
        if(numEntries == null)
        {
          numEntries = 0;
        }
        
        allUsers.put(entry.getUserId(), numEntries+1);    
      }
    }
    
    // put hashmap data into array (for easy mergesort momentarily)
    UserEntries[] userEntriesList = new UserEntries[allUsers.size()];
    int i = 0;
    for(Long user : allUsers.keySet())
    {
      Integer numEntries = allUsers.get(user);

      userEntriesList[i] = new UserEntries(user, numEntries);
      i++;
    }
    
    // mergesort the list (puts high numEntries before low numEntries)
    // always O(nlog(n)) time, O(n) space
    Arrays.sort(userEntriesList);
    
    if(maxUsersToPrint > userEntriesList.length)
    {
      maxUsersToPrint = userEntriesList.length;
    }
    
    // do the printing
    for(int index = 0; index < maxUsersToPrint; index++)
    {
      long user_id = userEntriesList[index].user_id;
      int entries = userEntriesList[index].entries;
      System.out.println("" + (index+1) + ". user" + user_id + ": " + entries);
    }
  }
  
  
  
  /**
   * Returns the percentage of piece_id's with error statuses that meet or
   * exceed errorThreshold.
   */
  public double getPercentageOfPiecesWithErrors(int errorThreshold)
  {
    if(errorThreshold < 0)
    {
      throw new IllegalArgumentException("WorkEntryList.getPercentageOfPiecesWithErrors() does not accept negative arguments.");
    }
    
    //first, make a hashmap with key:piece_id and value:errorAmount
    // average O(1) insertion/retrieval
    HashMap<Long, Integer> pieceList = new HashMap<Long, Integer>();    
    for (WorkEntry entry : entryList)
    {        
      if(entry.getPieceId() > -1)
      { 
        Integer numErrors = pieceList.get(entry.getPieceId());        
        if(numErrors == null)
        {
          numErrors = 0;
        }
        
        // if the status ends in 3, that means there's an error
        boolean hasError = (entry.getStatus()%10 == 3);        
        if(hasError)
        {
          numErrors++;
        }

        pieceList.put(entry.getPieceId(), numErrors);    
      }
    }
    
    // next, count the values meeting threshold
    int piecesMeetingErrorThreshold = 0;
    for(Integer errors : pieceList.values())
    {
      if(errors >= errorThreshold)
      {
        piecesMeetingErrorThreshold++;
      }
    }
    
    // finally, calculate percentage
    double percentageOfPiecesWithErrors = ((double)piecesMeetingErrorThreshold) / pieceList.size();    
    return percentageOfPiecesWithErrors;    
  }
  
  
  /**
   * Returns the average time a piece spends in a specific status.
   * Function written to answer this question:
   *   "On average, how long does a piece spend in status 8951?"
   * I'm interpreting this as:  totalTimeAllPiecesSpentInStatus / totalPieces = result
   */
  public long getAverageTimeSpentInStatus(long status)
  {    
    // average O(1) insertion/retrieval
    HashSet<Long> allPieces = new HashSet<Long>();
    long totalTime = 0;    
    for(WorkEntry entry : entryList)
    {
      if(entry.getStatus() == status && entry.getEndTime() > 0)
      {
        totalTime += entry.getEndTime() - entry.getStartTime(); 
      }
      
      allPieces.add(entry.getPieceId());
    }
      
    long average = totalTime / allPieces.size();
    
    return average;
  }
  
  
  
  /**
   * Returns the most common path for a piece to follow through the system
   */
  public long[] getMostCommonPath()
  {
    // look through all entries, make paths for each piece
    // average O(1) insertion/retrieval
    HashMap<Long, StatusPath> pathList = new HashMap<Long, StatusPath>();    
    for(WorkEntry entry : entryList)
    {       
      StatusPath path = pathList.get(entry.getPieceId());
      
      if(path == null)
      {
        path = new StatusPath();
      }
      
      // add status and associated time to path for this piece
      path.put(entry.getStartTime(), entry.getStatus());      
      // put updated path into the path list
      pathList.put(entry.getPieceId(), path);      
    }      
      
    
    // iterate through all pieces, make map of paths and their respective frequencies
    // while doing this, keep track of the path with the highest tally
    int highestTally = 0;
    StatusPath mostCommonPath = null;
    // average O(1) insertion/retrieval
    HashMap<StatusPath, Integer> pathTallies = new HashMap<StatusPath, Integer>();
    for(long piece_id : pathList.keySet())
    {
      StatusPath path = pathList.get(piece_id);
      int tally = 1;
      if(pathTallies.get(path) != null)
      {
        tally += pathTallies.get(path);
      }      
      pathTallies.put(path, tally);
      
      if(tally > highestTally)
      {
        highestTally = tally;
        mostCommonPath = path;
      }
    } 
    
    return mostCommonPath.getPathAsArray();
  }
  
  
  
  
    
  /**
   * Application method to run tasks.
   */
  public static void main(String[] args){
    
    System.out.println("Beginning program.");
    
    WorkEntryList list = new WorkEntryList();
    list.createListFromFile("test_data.json");        
    System.out.println("List successfully populated from file.");   
    
    long num = list.getNumberOfUniqueStatuses();    
    System.out.println("The number of unique statuses in the list is " + num);
    
    int limit = 5;
    System.out.println("Here are the " + limit + " user's with the most entries: ");
    list.printMostActiveUsers(limit);
    
    int errorThreshold = 2;
    double percentage = list.getPercentageOfPiecesWithErrors(errorThreshold);
    System.out.println("The percentage of entries with at least " + errorThreshold + " errors is " + percentage);
  
    long status = 8951;    
    long time = list.getAverageTimeSpentInStatus(status);
    System.out.println("The average time a piece spends in status " + status + " is " + time);
    
    long[] path = list.getMostCommonPath();
    System.out.println("The most common path through the system is:");
    for(long i : path)
    {
      System.out.print("" + i + " ");
    }
    System.out.println(); 
  } 
  
}
/**
 * WorkEntryList.java end
 * Written in Java
 */ 

