import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hashmapp {
     HashMap<String, List<String>> filmSkuespillere = new HashMap<>();

     public static void main(String[] args) {
        Hashmapp h = new Hashmapp();
        h.filmSkuespillere.put("hei", new ArrayList<>());
         h.filmSkuespillere.put("hei2", new ArrayList<>());
        h.filmSkuespillere.get("hei").add("kskas");
         h.filmSkuespillere.get("hei").add("kskr");
         h.filmSkuespillere.get("hei").add("kswkr");
          h.filmSkuespillere.get("hei2").add("kswk3r");
        System.out.println(h.filmSkuespillere.values());

     }
}
