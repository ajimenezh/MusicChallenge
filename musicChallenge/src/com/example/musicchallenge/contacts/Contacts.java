package com.example.musicchallenge.contacts;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;


/** Helper class to store all the contact information on the phone.
 */
public class Contacts{
	
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	
	/**
	 * Constructor of the Contacts class.
	 * Find all the contacts information in the phone.
	 * @param context Activity context.
	 */
	public Contacts(Context context) {
		ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
		
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                     Cursor pCur = cr.query(
                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                               null,
                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                     while (pCur.moveToNext()) {
                         String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         
                         Contact contact = new Contact(name, phoneNo);
                         contacts.add(contact);
                         
                     }
                    pCur.close();
                }
            }
        }
	}
	
	/**
	 * Get all the contact information.
	 * @return ArrayList<Contact> with all the information.
	 */
	public ArrayList<Contact> getContacts() {
		return this.contacts;
	}
}

