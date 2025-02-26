import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Skuespiller {
    String id;
    String navn;
    boolean besokt;
    List<Skuespiller> naboer;
    List<String> filmer; // en liste med film-id, som skuespilleren spilt.
    double distanse = Double.MAX_VALUE;
    Skuespiller forrige;

    public Skuespiller(String id, String navn) {
        this.id = id;
        this.navn = navn;
        this.besokt = false;
        this.naboer = new ArrayList<>();
        this.filmer = new ArrayList<>();
    }

    public String toString() {
        return navn;
    }
}

class Film {
    String id;
    String navn;
    double rating;

    public Film(String id, String navn, double rating) {
        this.id = id;
        this.navn = navn;
        this.rating = rating;
    }
}

class Kant {
    Skuespiller startNode;
    Skuespiller sluttNode;
    Film film;

    public Kant(Skuespiller startNode, Skuespiller sluttNode, Film film) {
        this.startNode = startNode;
        this.sluttNode = sluttNode;
        this.film = film;
    }

    public String toString() {
        return "film: " + film.navn + " navn1: " + startNode.navn + " navn2: " + sluttNode.navn;
    }
}

class Graf {
    Map<String, Skuespiller> grafs;
    ArrayList<Film> antFilmer = new ArrayList<>(); // alle filmer i filen.
    ArrayList<Skuespiller> antNoder = new ArrayList<>(); // alle skuespillere i filen.
    ArrayList<Kant> kanter = new ArrayList<>(); // alle kanter i grafen.

    HashMap<String, Kant> mapKanter = new HashMap<>(); // Hver noekkel inneholder en kant hvor to skuespillere har spilt i samme film. oppg2.
    HashMap<Skuespiller, Double> distanse = new HashMap<>(); // Distansen for dijkstra-algortimen. Oppg3.
    HashMap<String, ArrayList<Kant>> sammeFilmer = new HashMap<>(); // noekkel = to skuespillere. Value = filmene de spiller sammen. 
    HashMap<Skuespiller, Boolean> besokt = new HashMap<>(); // For oppg4, hvor jeg sjekker om skluespilleren har blitt besokt.
    HashMap<Integer, Integer> komponent = new HashMap<>(); // nokkel er storrelse på komponent, og value er hvor mange slike det er av den størrelen.

    static int teller = 0; // teller storrelsen på komponeneten.

    public Graf() {
        this.grafs = new HashMap<>();
    }
    public void leggTilKant(String idU, String uNavn, String idV, String vNavn, Film f) {
        Skuespiller uNode = hentNode(idU, uNavn);
        Skuespiller vNode = hentNode(idV, vNavn);
        String kantNokkel = uNode.id + "-" + vNode.id; // Noekkel kan enten være en av de to.
        String kantNokkelM = vNode.id + "-" + uNode.id;
        Kant kant = new Kant(uNode, vNode, f);
        kanter.add(kant);
    
        if (!(sammeFilmer.containsKey(kantNokkel))) {
            if (!(sammeFilmer.containsKey(kantNokkelM))) {
                sammeFilmer.put(kantNokkel, new ArrayList<>());
                sammeFilmer.get(kantNokkel).add(kant);
            } else {
                sammeFilmer.get(kantNokkelM).add(kant);
            }
        } else {
            sammeFilmer.get(kantNokkel).add(kant);
        }
        mapKanter.put(kantNokkel, kant);

        uNode.naboer.add(vNode);
        vNode.naboer.add(uNode);
    }

    public Skuespiller hentNode(String id, String navn) {
        if (!grafs.containsKey(id)) {
            grafs.put(id, new Skuespiller(id, navn));
        }
        return grafs.get(id);
    }

    public ArrayList<Skuespiller> kortVei(Skuespiller start, Skuespiller slutt) {
        ArrayList<Skuespiller> besokt = new ArrayList<>();
        Queue<Skuespiller> queue = new LinkedList<>();
        queue.add(start);
        besokt.add(start);
        HashMap<Skuespiller, Skuespiller> foreldre = new HashMap<>();
        while (!queue.isEmpty()) {
            Skuespiller tmp = queue.poll();
            for (Skuespiller skuespiller : tmp.naboer) {
                if (!besokt.contains(skuespiller)) {
                    besokt.add(skuespiller);
                    queue.add(skuespiller);
                    skuespiller.distanse = tmp.distanse + 1;
                    foreldre.put(skuespiller, tmp); // noekkel er skuespiller, og value er hvor skuespiller kom fra.
                }
                if (skuespiller.equals(slutt)) {
                    return lagVei(foreldre, slutt);
                }
            }
        }
        return null;
    }

    public ArrayList<Skuespiller> lagVei(HashMap<Skuespiller, Skuespiller> foreldre, Skuespiller slutt) {
        ArrayList<Skuespiller> vei = new ArrayList<>(); // nodene for den korteste veien.
        ArrayList<Kant> lilKanter = new ArrayList<>(); // kantene som forbinder den korteste stien mellom start og
                                                       // slutt.
        Skuespiller skuespiller = slutt;
        while (skuespiller != null) {
            vei.add(skuespiller);
            Skuespiller forelder = foreldre.get(skuespiller);
            if (forelder != null) {
                if (mapKanter.get(forelder.id + "-" + skuespiller.id) != null) {
                    Kant kant = mapKanter.get(forelder.id + "-" + skuespiller.id);
                    lilKanter.add(kant);
                } else {
                    Kant kant = mapKanter.get(skuespiller.id + "-" + forelder.id);
                    lilKanter.add(kant);
                }

            }
            skuespiller = forelder;
        }
        Collections.reverse(vei); // Reverserer listene for å få riktig rekkefølge på kanter og noder.
        Collections.reverse(lilKanter); 
                                        
        for (int i = 0; i < vei.size(); i++) {
            System.out.println(vei.get(i));
            if (lilKanter.size() > i) {
                System.out.println(lilKanter.get(i));
            }
        }
        return vei;
    }

    // Har laget en sluttnode, og startnode hvor man kan printe ut riktig. 
    public HashMap<Skuespiller, Double> dijkstra(Skuespiller startNode) {
        PriorityQueue<Skuespiller> queue = new PriorityQueue<>(Comparator.comparing(distanse::get)); // fikk denne fra chatgpt. Fant ikke en veldig effektiv maate aa sortere paa self.
        queue.add(startNode);
        distanse.put(startNode, 0.0); 

        while (!queue.isEmpty()) {
            Skuespiller tmp = queue.poll();
            queue.remove(tmp);
            for (Skuespiller u : tmp.naboer) {
                Kant bestKant = finnBesteRating(tmp, u);
                double nyDistanse = distanse.get(tmp) + (10 - bestKant.film.rating);
                if (nyDistanse < distanse.get(u)) {
                    distanse.put(u, nyDistanse);
                    u.forrige = tmp;
                    queue.add(u);
                }
            }
        }
        return distanse;
    }

    public Kant finnBesteRating(Skuespiller u, Skuespiller v) {
        String id = u.id + "-" + v.id;
        String idM = v.id + "-" + u.id;
        Kant storstKant = null;
        double storstRating = 0;

        ArrayList<Kant> filmer = sammeFilmer.get(id);
        ArrayList<Kant> filmerM = sammeFilmer.get(idM);
        if (filmer != null) {
            for (Kant kant : filmer) {
                if (kant.film.rating > storstRating) {
                    storstRating = kant.film.rating;
                    storstKant = kant;
                }
            }
        }
        if (filmerM != null) {
            for (Kant kant : filmerM) {
                if (kant.film.rating > storstRating) {
                    storstRating = kant.film.rating;
                    storstKant = kant;
                }
            }
        }
        return storstKant;
    }

    public int DFSVisit(Skuespiller startNode) {
        teller++;
        besokt.put(startNode, true);
        for (Skuespiller v : startNode.naboer) {
            if (besokt.get(v) == false) {
                DFSVisit(v);
            }
        }
        return teller;
    }

    public void DFSFull() {
        // besokt: Alle skuespillere, hvor alle starter med false.
        for (Skuespiller x : besokt.keySet()) {
            if (besokt.get(x) == false) {
                int j = DFSVisit(x);
                teller = 0;
                if (komponent.containsKey(j)) {
                    int i = komponent.get(j) + 1;
                    komponent.put(j, i);
                }
                else{
                    komponent.put(j, 1);
                }
            }
        }
    }
}

public class ByggGraf {
    public static void main(String[] args) {
        Graf graf = new Graf();
        HashMap<String, ArrayList<Skuespiller>> filmSkuespillere = new HashMap<>();
        try {
            File myObj = new File(args[0] + ".tsv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] info = data.split("\t");
                Film nyFilm = new Film(info[0], info[1], Double.parseDouble(info[2]));
                graf.antFilmer.add(nyFilm);
                filmSkuespillere.put(info[0], new ArrayList<>());
            }
            myReader.close();
            File fil2 = new File("actors.tsv");
            Scanner scanner2 = new Scanner(fil2);

            while (scanner2.hasNextLine()) {
                String data = scanner2.nextLine();
                String[] info = data.split("\t");
                if (info.length <= 3) {
                    Skuespiller skuepsiller = new Skuespiller(info[0], info[1]);
                    graf.antNoder.add(skuepsiller);
                    skuepsiller.filmer.add(info[2]);
                    graf.grafs.put(info[0], skuepsiller);
                    graf.distanse.put(skuepsiller, skuepsiller.distanse);
                    graf.besokt.put(skuepsiller, false);
                    if (filmSkuespillere.containsKey(info[2])) {
                        ArrayList<Skuespiller> hentetArrayList = filmSkuespillere.get(info[2]);
                        hentetArrayList.add(skuepsiller);
                    }
                }
                if (info.length > 3) {
                    int teller = 2;
                    Skuespiller skuepsiller = new Skuespiller(info[0], info[1]);
                    graf.antNoder.add(skuepsiller);
                    graf.grafs.put(info[0], skuepsiller);
                    graf.distanse.put(skuepsiller, skuepsiller.distanse);
                    graf.besokt.put(skuepsiller, false);
                    while (info.length > teller) {
                        skuepsiller.filmer.add(info[teller]);
                        if (filmSkuespillere.containsKey(info[teller])) {
                            filmSkuespillere.get(info[teller]).add(skuepsiller);
                        }
                        teller++;
                    }
                }
            }
            scanner2.close();
            for (Film film : graf.antFilmer) {
                ArrayList<Skuespiller> liste = filmSkuespillere.get(film.id);
               
                for (int x = 0; x < liste.size(); x++) {
                    for (int y = x + 1; y < liste.size(); y++) {
                        graf.leggTilKant(liste.get(x).id, liste.get(x).navn, liste.get(y).id, liste.get(y).navn, film);
                    }
                }
            }
            System.out.println(graf.antNoder.size());
            System.out.println(graf.kanter.size());
            System.out.println(graf.distanse.size());
          
            // graf.DFSFull();
            // System.out.println(graf.komponent);

            graf.kortVei(graf.grafs.get("nm2255973"), graf.grafs.get("nm0000460")); // Oppg2

            HashMap<Skuespiller,Double> oppg3 = graf.dijkstra(graf.grafs.get("nm0637259")); // Kaller paa oppg3.

            // printer ut den korteste veien. Oppg 3.
            Skuespiller tmp = graf.grafs.get("nm0931324"); // Sluttnoden hvor man vil komme til.
            Skuespiller slutt = graf.grafs.get("nm0637259"); // Startnoden.
            while (slutt != tmp) {
                 System.out.println(tmp);
                 tmp = tmp.forrige;
             }
             System.out.println(slutt);
             System.out.println("Total vekt:" + (oppg3.get(graf.grafs.get("nm0931324"))));

         } catch (FileNotFoundException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
        }

    }
}