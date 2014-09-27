package fr.laas.knxsimulator.com;

/**
 * Observable interface
 * 
 * @author Guillaume Garzone
 * 
 */
public interface Observable {
	
	public void addObserver(Observer o);

	public void removeObserver(Observer o);

	public void notifyObservers();
}
