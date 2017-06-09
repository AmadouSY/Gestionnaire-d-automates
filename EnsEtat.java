import java.util.HashSet;
import java.util.Set;

/************ CLASSE ENSETAT **************/

/**
 * Representation d'un ensemble d'etats
 * @see Etat
 */
public class EnsEtat extends HashSet<Etat> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Ensemble d'etat vide
	 */
	public EnsEtat() {
		super();
	}

	/**
	 * Ensemble d'etat successeur de par la lettre en parametre
	 * @param c lettre de la transition
	 * @return l'ensemble d'etat atteignable par la lettre
	 */
	public EnsEtat succ(char c){
		EnsEtat a = new EnsEtat();
		for(Etat etat : this){
			EnsEtat tmp = etat.succ(c);
			a.addAll(tmp);
		}
		return a;
	}

	/**
	 * Ensemble d'etat successeur sur tout l'alphabet
	 * @return l'ensemble d'etat atteignable sur tout l'alphabet
	 */
	public EnsEtat succ(){
		EnsEtat a = new EnsEtat();
		for(Etat etat : this){
			EnsEtat sorties = etat.succ();
			a.addAll(sorties);
		}
		return a;
	}

	/**
	 * Test si l'ensemble d'etat contient un etat terminal
	 * @return resultat du test
	 */
	public boolean contientTerminal(){
		for(Etat etat : this){
			if(etat.isTerm()) return true;
		}
		return false;
	}
	
	/**
	 * Recupere un ensemble des lettre du langage de l'ensemble d'etat
	 * @return ensemble de lettre
	 */
	public Set<Character> alphabet(){
		Set<Character> a = new HashSet<Character>();
		for(Etat etat : this){
			for(Character c : etat.alphabet()){
				a.add(c);
			}
		}
		return a;
	}

	/**
	 * Test si deux ensembles d'etats ont les memes etats
	 * @param e l'ensemble d'etat auquel comparer this
	 * @return resultat du test
	 */
	public boolean compare(EnsEtat e){
		for(Etat etat : this){
			if(e.getEtat(etat.hashCode()) == null) return false;
		}
		
		for(Etat etat : e){
			if(this.getEtat(etat.hashCode()) == null) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null || getClass() != obj.getClass()){
			return false;
		}else{
			final EnsEtat other = (EnsEtat) obj;
			if(this.isEmpty() && other.isEmpty()) return true;
			for(Etat etat : this){
				if(other.getEtat(etat.hashCode()) == null) return false;
			}
	
			for(Etat etat : other){
				if(this.getEtat(etat.hashCode()) == null) return false;
			}
			return true;
		}
	}

	@Override
	public String toString(){
		String res = "";
		res += this.size() + " Etats\n";
		for(Etat etat : this){
			res += "\n"+etat.toString();
		}
		return res;
	}

	/**
	 * Representation du contenu de l'ensemble d'etats
	 * @return une chaine de caracteres
	 */
	public String listEtats(){
		String res = "(";
		for(Etat etat : this){
			res += etat.hashCode()+",";
		}
		res = res.substring(0, res.length()-1);
		return res+")";
	}

	/**
	 * Recupere l'etat avec l'id en parametre, null s'il n'y est pas
	 * @param id l'id de l'etat a chercher
	 * @return l'etat
	 */
	public Etat getEtat(int id){
		for(Etat etat : this){
			if(etat.hashCode() == id){
				return etat;
			}
		}
		return null;
	}

}
