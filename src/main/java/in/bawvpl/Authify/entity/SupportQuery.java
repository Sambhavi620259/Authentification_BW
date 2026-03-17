
package in.bawvpl.Authify.entity;

public class SupportQuery {

    private Long id;
    private String userId;
    private Long applicationId;
    private String queryText;
    private String answer;
    private String status;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}

    public Long getApplicationId(){return applicationId;}
    public void setApplicationId(Long applicationId){this.applicationId=applicationId;}

    public String getQueryText(){return queryText;}
    public void setQueryText(String queryText){this.queryText=queryText;}

    public String getAnswer(){return answer;}
    public void setAnswer(String answer){this.answer=answer;}

    public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;}
}
