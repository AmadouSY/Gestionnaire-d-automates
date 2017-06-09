import java.util.HashMap;
import java.util.Set;
import java.util.Map;

/**
 * Representation de l'etat de l'automate
 */
public class Etat {
	
	HashMap<Character, EnsEtat> transitions;
	boolean init;
	boolean term;
	int id;

	/**
	 * Construit l'etat vide
	 */
	public Etat() {
		this.transitions = new HashMap<Character, EnsEtat>();
	}

	/**
	 * Construit l'etat selon son id
	 * @param id identifiant de l'etat
	 */
	public Etat(int id) {
		this.transitions = new HashMap<Character, EnsEtat>();
		this.id = id;
		this.term = false;
		this.init = false;
	}

	/**
	 * Construit l'etat selon son id et ses caracteristiques (final-terminal)
	 * @param init vrai si initial
	 * @param term vrai si terminal
	 * @param id identifiant de l'etat
	 */
	public Etat(boolean init, boolean term, int id) {
		this.transitions = new HashMap<Character, EnsEtat>();
		this.init = init;
		this.term = term;
		this.id = id;
	}

	/**
	 * Recupere les successeurs de la transition par la lettre en parametre
	 * @param c lettre de la transition
	 * @return un ensemble d'etats des etats successeurs
	 */
	public EnsEtat succ(char c){
		if(transitions.containsKey(c)){
			return transitions.get(c);
		}
		return new EnsEtat();
	}

	/**
	 * Recupere les successeurs par toutes les transitions
	 * @return un ensemble d'etats des etats successeurs
	 */
	public EnsEtat succ(){
		if(!transitions.isEmpty()){
			EnsEtat tmp = new EnsEtat();
			for(EnsEtat etats: transitions.values()){
				tmp.addAll(etats);
			}
			return tmp;
		}
		return new EnsEtat();
	}

	/**
	 * Ajoute une transition
	 * @param c lettre de la transition
	 * @param e etat atteint par la transition
	 */
	public void ajouteTransition(char c, Etat e){
		if(transitions.containsKey(c)){
			EnsEtat tmp = transitions.get(c);
			tmp.add(e);
		}else{
			EnsEtat tmp = new EnsEtat();
			tmp.add(e);
			transitions.put(c, tmp);
		}
	}

	/**
	 * Recupere l'alphabet de l'etat
	 * @return ensemble des lettres des transitions
	 */
	public Set<Character> alphabet(){
		return transitions.keySet();
	}

	/**
	 * Getter initial
	 * @return boolean
	 */
	public boolean isInit() {
		return init;
	}

	/**
	 * Getter acceptant
	 * @return boolean
	 */
	public boolean isTerm() {
		return term;
	}
	
	/**
	 * Setter initial
	 * @param init valeur d'inital
	 */
	public void setInit(boolean init) {
		this.init = init;
	}

	/**
	 * Setter acceptant
	 * @param term valeur acceptant
	 */
	public void setTerm(boolean term) {
		this.term = term;
	}

	/**
	 * Getter des transitions
	 * @return les transitions
	 */
	public HashMap<Character, EnsEtat> getTransitions(){
		return transitions;
	}


	@Override
	public String toString(){
		String res = "";
		res += id+"";
		res += (init)? " initial": "";
		res += (term)? " terminal" : "";
		res += "\n";
		
		for(Map.Entry<Character, EnsEtat> entry : transitions.entrySet()){
			res += entry.getKey().charValue();
			for(Etat etat : entry.getValue()){
				res += " "+etat.id;
			}
			res += "\n";
		}
		return res;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		} else {
			final Etat other = (Etat) obj;
			return (id == other.id);
		}
	}

	
}
