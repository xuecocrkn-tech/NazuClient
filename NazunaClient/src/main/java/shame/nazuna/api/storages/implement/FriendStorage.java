package shame.nazuna.api.storages.implement;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 public class FriendStorage
 {
   
   public void add(String friend) {
     if (!friend.isEmpty()) this.friends.add(friend); 
   }
   
   public void remove(String friend) {
     this.friends.remove(friend);
   }
   
   public void clear() {
     this.friends.clear();
   }
   
   public boolean isFriend(String friend) {
     return this.friends.contains(friend);
   }
   
   public boolean isEmpty() {
     return this.friends.isEmpty();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\FriendStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */