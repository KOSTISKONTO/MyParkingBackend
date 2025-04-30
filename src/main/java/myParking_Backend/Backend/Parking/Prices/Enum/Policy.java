package myParking_Backend.Backend.Parking.Prices.Enum;

public enum Policy {
   FlatCost("FlatCost"),
   CostByHour("CostByHour"),
   ByHour("ByHour"),
   ByHourCustom("ByHourCustom"),
   ByLocalTime("ByLocalTime"),
   ByLocalTimeCustom("ByLocalTimeCustom");

   private final String label;

   Policy(String label) {
      this.label = label;
   }

   public String getLabel() {
      return label;
   }


   }
