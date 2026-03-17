
package in.bawvpl.Authify.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/support")
public class SupportController {

    private Map<Long, Map<String,Object>> db = new HashMap<>();
    private long id=1;

    @PostMapping("/query")
    public Map<String,Object> create(@RequestBody Map<String,Object> body){
        body.put("id",id++);
        body.put("status","OPEN");
        db.put((Long)body.get("id"),body);
        return body;
    }

    @GetMapping("/user/{userId}")
    public List<Map<String,Object>> user(@PathVariable String userId){
        List<Map<String,Object>> list=new ArrayList<>();
        for(Map<String,Object> m:db.values()){
            if(userId.equals(m.get("userId"))) list.add(m);
        }
        return list;
    }

    @PutMapping("/respond/{id}")
    public Map<String,Object> respond(@PathVariable Long id,@RequestBody Map<String,Object> body){
        Map<String,Object> q=db.get(id);
        q.put("answer",body.get("answer"));
        q.put("status","CLOSED");
        return q;
    }
}
