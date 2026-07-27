package shame.nazuna.client.modules.impl.render.base.implement;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 class StaffData
 {
   String status;
   List<StaffList.PrefixSegment> segments;
   float prefixWidth12;
   float prefixWidth14;
   float nameWidth12;
   float nameWidth14;
   
   StaffData(String status) {
     this.status = status;
     this.segments = new ArrayList<>();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\base\implement\StaffList$StaffData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */