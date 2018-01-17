/**
 * WorkEntry.java begin
 * Written in Java
 */ 

/**
 * Represents the history of pieces of work moving through an automated system.
 * 
 * id: A unique ID for this history entry
 * piece_id: The piece of work being operated on
 * status: Number indicating the operation being performed on the piece
 * user_id: ID of the user that performed the operation in this entry
 * start_time: Time that the piece began being processed in the status
 * end_time: Time that the piece finished being processed in the status;
 *           the difference between end_time and start_time indicates how long the piece spent in the status 
 */
public class WorkEntry implements Comparable<WorkEntry>{
  
  // all valid values are positive; negative values represent a lack of an actual value in the record
  private long id;
  private long piece_id;
  private long status;
  private long user_id;
  private long start_time;
  private long end_time;
  
  /* constructor */
  public WorkEntry(long id, long piece_id, long status, long user_id, long start_time, long end_time)
  {
    this.id = id;
    this.piece_id = piece_id;
    this.status = status;
    this.user_id = user_id;
    this.start_time = start_time;
    this.end_time = end_time;
  }   
  
  
  /* compareTo, equals, hashCode all use unique id field */
  
  public int compareTo(WorkEntry other)
  {
    return (int)(this.id - other.id);
  }
  
  @Override
  public boolean equals(Object object)
  {    
    if(object == null || !(object instanceof WorkEntry))
    {
      return false;
    }
    
    final WorkEntry other = (WorkEntry)object;    
    return this.compareTo(other) == 0;   
  }
  
  @Override
  public int hashCode()
  {
    int result = 13;
    int c = (int)(id ^ (id >>> 32));
    result = 37 * result + c;
    return result;
  }
  
  
  /* getters */
  
  public long getId()
  {
    return id;
  }
  
  public long getPieceId()
  {
    return piece_id;
  }
  
  public long getStatus()
  {
    return status;
  }
  
  public long getUserId()
  {
    return user_id;
  }
  
  public long getStartTime()
  {
    return start_time;
  }
  
  public long getEndTime()
  {
    return end_time;
  }
  
  /* setters */
  
  public void setId(long num)
  {
    id = num;
  }
  
  public void setPieceId(long num)
  {
    piece_id = num;
  }
  
  public void setStatus(long num)
  {
    status = num;
  }
  
  public void setUserId(long num)
  {
    user_id = num;
  }
  
  public void setStartTime(long num)
  {
    start_time = num;
  }
  
  public void setEndTime(long num)
  {
    end_time = num;
  }
   
 
  
}
/**
 * WorkEntry.java end
 * Written in Java
 */ 

