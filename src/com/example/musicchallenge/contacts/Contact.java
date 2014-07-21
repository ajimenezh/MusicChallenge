package com.example.musicchallenge.contacts;

/** Class to define a Contact
 */
public class Contact{
	private String name;
	private String phoneNumber;
	
	/** Contact constructor.
	 * 
	 */
	public Contact() {
		
	}
	
	/** Contact constructor
	 * 
	 * @param name Name stored in the phone.
	 * @param phoneNumber Phone number.
	 */
	public Contact(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	
	public String getName() {
		return this.name;
	}
}