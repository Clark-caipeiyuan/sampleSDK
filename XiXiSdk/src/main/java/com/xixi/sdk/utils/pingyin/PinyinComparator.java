package com.xixi.sdk.utils.pingyin;

import java.util.Comparator;
import java.util.Locale;

import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.model.LLXmlNode;

public class PinyinComparator implements Comparator<LLXmlNode> {
	
	private static PinyinComparator instance ; 
	public static PinyinComparator getInstance() { return instance ; } 
	
	private PinyinComparator() {} 
	static { 
		instance = new PinyinComparator() ; 
	}
	
	@Override
	public int compare(LLXmlNode lhs, LLXmlNode rhs) { 
		return sort((LLBuddy)lhs, (LLBuddy)rhs);
	}

	private int sort(LLBuddy lhs, LLBuddy rhs) {
		char lhs_ascii = lhs.getFirstPinYin();
		char rhs_ascii = rhs.getFirstPinYin();
		String l = lhs.getPinYin().toUpperCase(Locale.ENGLISH);
		String r = rhs.getPinYin().toUpperCase(Locale.ENGLISH);
		if ( lhs_ascii < 'A'){
			if ( rhs_ascii < 'A' || rhs_ascii > 'Z') { 
				return l.compareTo(r);
			}
			else {
				return 1 ; 
			}
		} 
		else if ( lhs_ascii >= 'A' && lhs_ascii <= 'Z') {
			if ( rhs_ascii < 'A' || rhs_ascii > 'Z') { 
				return -1 ; 
			}
			else { 
				return l.compareTo(r);
			}
		}
		else { 
			if ( rhs_ascii < 'A' || rhs_ascii > 'Z') { 
				return l.compareTo(r);
			} 
			else { 
				return 1 ; 
			}
		} 
	}

}
