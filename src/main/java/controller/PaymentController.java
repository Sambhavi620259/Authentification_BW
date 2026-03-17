
package in.bawvpl.Authify.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1.0/payment")
public class PaymentController {

    private Map<Long, Map<String,Object>> db = new HashMap<>();
    private long id=1;

    @PostMapping
    public Map<String,Object> create(@RequestBody Map<String,Object> body){
        body.put("paymentId",id++);
        db.put((Long)body.get("paymentId"), body);
        return body;
    }

    @GetMapping("/user/{userId}")
    public List<Map<String,Object>> userPayments(@PathVariable String userId){
        List<Map<String,Object>> list = new ArrayList<>();
        for(Map<String,Object> m:db.values()){
            if(userId.equals(m.get("userId"))) list.add(m);
        }
        return list;
    }

    @PutMapping("/{id}")
    public Map<String,Object> update(@PathVariable Long id,@RequestBody Map<String,Object> body){
        body.put("paymentId",id);
        db.put(id,body);
        return body;
    }
}
