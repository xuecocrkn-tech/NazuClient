package shame.nazuna.api.storages.implement;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 public class StaffStorage
 {
   
   public void add(String friend) {
     if (!friend.isEmpty()) this.staffs.add(friend); 
   }
   
   public void remove(String friend) {
     this.staffs.remove(friend);
   }
   
   public void clear() {
     this.staffs.clear();
   }
   
   public boolean isStaff(String friend) {
     return this.staffs.contains(friend);
   }
   
   public boolean isEmpty() {
     return this.staffs.isEmpty();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\StaffStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */