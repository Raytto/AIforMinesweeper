package test;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import tools.Dot;

public class test4 {

	public test4() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
//		try {
//			Robot rb = new Robot();
//			rb.mouseMove(150, 250);
//			//Thread.sleep(1000);
//			rb.mousePress(InputEvent.BUTTON3_MASK);
//			rb.mouseRelease(InputEvent.BUTTON3_MASK);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		//first String is the toString of HashMap<String,Object>
		//HashMap<String,Object> contain DOTNUM:Integer;MINENUM:Integer
		HashMap<String,HashMap<String,Object>> allCollections;
		
		HashMap<String,String> h1=new HashMap<String,String>();
		HashMap<String,String> h2=new HashMap<String,String>();
		h1.put("a", "a1");
		h1.put("b", "b1");
		h1.put("ab", "b1");
		h2.put("b", "b1");
		h2.put("ab", "tt1");
		h2.put("a","a1");
		System.out.println(h1);
		System.out.println(h2);
		System.out.println(h2.get("tt"));
//		t.put(lo, 1)
	}

}
