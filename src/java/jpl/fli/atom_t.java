/*This code is copyrighted by Teknowledge (c) 2003.
It is released underthe GNU Public License <http://www.gnu.org/copyleft/gpl.html>.
Users ofthis code also consent, by use of this code, to credit Teknowledge in any
writings, briefings,publications, presentations, or other representations of any
software which incorporates, builds on, or uses this code.*/ 

 
package jpl.fli;



//----------------------------------------------------------------------/
// atom_t
/**
 * An atom_t is a simple extension of a term_t.
 * 
 * <hr><i>
 * Copyright (C) 1998  Fred Dushin<p>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.<p>
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library Public License for more details.<p>
 * </i><hr>
 * @author  Fred Dushin <fadushin@syr.edu>
 * @version $Revision$
 */
// Implementation notes:  
// 
//----------------------------------------------------------------------/
public class atom_t 
extends LongHolder
{
	//------------------------------------------------------------------/
	// toString
	/**
	 * The String representation of an atom_t is just the atom's name.
	 * 
	 * @return  atom's name
	 */
	// Implementation notes:  
	// 
	//------------------------------------------------------------------/
	public String
	toString()
	{
		return Prolog.atom_chars( this );
	}
}

//345678901234567890123456789012346578901234567890123456789012345678901234567890
