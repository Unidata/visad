
//
// FormulaUtil.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 1998 Bill Hibbard, Curtis Rueden, Tom
Rink and Dave Glowacki.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 1, or (at your option)
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License in file NOTICE for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package visad.formula;

import java.lang.reflect.*;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import visad.*;

/** Contains a variety of useful methods related to the
    visad.formula package.<P> */
public class FormulaUtil {

  /** create a FormulaManager object with many commonly desired features */
  public static FormulaManager createStandardManager() {
    String[] binOps = {".", "^", "*", "/", "%", "+", "-"};
    int[] binPrec =   {200, 400, 600, 600, 600, 800, 800};
    String[] binMethods = {"visad.formula.FormulaUtil.dot(visad.Tuple," +
                                                         "visad.Real)",
                           "visad.Data.pow(visad.Data)",
                           "visad.Data.multiply(visad.Data)",
                           "visad.Data.divide(visad.Data)",
                           "visad.Data.remainder(visad.Data)",
                           "visad.Data.add(visad.Data)",
                           "visad.Data.subtract(visad.Data)"};
    String[] unaryOps = {"-"};
    int[] unaryPrec =   {500};
    String[] unaryMethods = {"visad.Data.negate()"};
    String[] functions = {"abs", "acos", "acosDegrees", "asin", "asinDegrees",
                          "atan", "atan2", "atanDegrees", "atan2Degrees",
                          "ceil", "combine", "cos", "cosDegrees", "derive",
                          "domainMultiply", "domainFactor", "exp", "extract",
                          "floor", "getSample", "linkx", "log", "max", "min",
                          "negate", "rint", "round", "sin", "sinDegrees",
                          "sqrt", "tan", "tanDegrees"};
    String[] funcMethods = {"visad.Data.abs()", "visad.Data.acos()",
                            "visad.Data.acosDegrees()", "visad.Data.asin()",
                            "visad.Data.asinDegrees()", "visad.Data.atan()",
                            "visad.Data.atan2(visad.Data)",
                            "visad.Data.atanDegrees()",
                            "visad.Data.atan2Degrees(visad.Data)",
                            "visad.Data.ceil()",
                            "visad.FieldImpl.combine(visad.Field[])",
                            "visad.Data.cos()", "visad.Data.cosDegrees()",
                            "visad.formula.FormulaUtil.derive(" +
                                "visad.Function, visad.formula.VRealType)",
                            "visad.FieldImpl.domainMultiply()",
                            "visad.formula.FormulaUtil.factor(" +
                                "visad.FieldImpl, visad.formula.VRealType)",
                            "visad.Data.exp()",
                            "visad.formula.FormulaUtil.extract(visad.Field," +
                                                              "visad.Real)",
                            "visad.Data.floor()",
                            "visad.formula.FormulaUtil.brackets(visad.Field," +
                                                               "visad.Real)",
                            "visad.formula.FormulaUtil.link(" +
                                "visad.formula.VMethod, java.lang.Object[])",
                            "visad.Data.log()",
                            "visad.Data.max(visad.Data)",
                            "visad.Data.min(visad.Data)",
                            "visad.Data.negate()", "visad.Data.rint()",
                            "visad.Data.round()", "visad.Data.sin()",
                            "visad.Data.sinDegrees()", "visad.Data.sqrt()",
                            "visad.Data.tan()", "visad.Data.tanDegrees()"};
    int implicitPrec = 200;
    String[] implicitMethods = {"visad.formula.FormulaUtil.implicit(" +
                                    "visad.Function, visad.Real)",
                                "visad.Function.evaluate(visad.RealTuple)"};
    FormulaManager f;
    try {
      f = new FormulaManager(binOps, binPrec, binMethods, unaryOps, unaryPrec,
                             unaryMethods, functions, funcMethods,
                             implicitPrec, implicitMethods);
    }
    catch (FormulaException exc) {
      return null;
    }
    return f;
  }

  /** evaluates the dot operator */
  public static Data dot(Tuple t, Real r) {
    Data d = null;
    try {
      d = t.getComponent((int) r.getValue());
    }
    catch (VisADException exc) { }
    catch (RemoteException exc) { }
    return d;
  }

  /** evaluate the derive function */
  public static Data derive(Function f, VRealType rt) {
    Data val = null;
    try {
      val = f.derivative(rt.getRealType(), Data.NO_ERRORS);
    }
    catch (VisADException exc) { }
    catch (RemoteException exc) { }
    return val;
  }

  /** evaluate the domainFactor function */
  public static visad.Field factor(FieldImpl f, VRealType rt) {
    visad.Field val = null;
    try {
      val = f.domainFactor(rt.getRealType());
    }
    catch (VisADException exc) { }
    catch (RemoteException exc) { }
    return val;
  }

  /** evaluate the extract function */
  public static Data extract(visad.Field f, Real r) {
    Data d = null;
    try {
      d = f.extract((int) r.getValue());
    }
    catch (VisADException exc) { }
    catch (RemoteException exc) { }
    return d;
  }

  /** evaluate the link function */
  public static Data link(VMethod m, Object[] o) {
    Data ans = null;
    if (o != null) {
      for (int i=0; i<o.length; i++) {
        // convert VRealTypes to RealTypes
        if (o[i] instanceof VRealType) {
          o[i] = ((VRealType) o[i]).getRealType();
        }
      }
    }
    try {
      ans = (Data) FormulaUtil.invokeMethod(m.getMethod(), o);
    }
    catch (ClassCastException exc) { }
    catch (IllegalAccessException exc) { }
    catch (IllegalArgumentException exc) { }
    catch (InvocationTargetException exc) { }
    return ans;
  }

  /** evaluate implicit function syntax; e.g., A1(5) or A1(A2) */
  public static Data implicit(Function f, Real r) {
    Data value = null;
    try {
      value = f.evaluate(r);
    }
    catch (VisADException exc) { }
    catch (RemoteException exc) { }
    return value;
  }

  /** evaluate the bracket function; e.g., A1[5] or A1[A2] */
  public static Data brackets(visad.Field f, Real r) {
    Data value = null;
    try {
      RealType rt = (RealType) r.getType();
      value = f.getSample((int) r.getValue());
    }
    catch (VisADException exc) { }
    catch (RemoteException exc) { }
    return value;
  }

  /** number of link variables that have been created */
  private static int linkNum = 0;

  /** do some pre-computation parsing to a formula */
  public static String preParse(String f, FormulaManager fm) {
    // remove spaces
    StringTokenizer t = new StringTokenizer(f, " ", false);
    String s = "";
    while (t.hasMoreTokens()) s = s + t.nextToken();
    if (s.equals("")) return s;

    // multi-pass pre-parse sequence
    String os;
    do {
      os = s;
      s = preParseOnce(os, fm);
    }
    while (!s.equals(os));
    return s;
  }

  /** used by preParse */
  private static String preParseOnce(String s, FormulaManager fm) {
    // convert to lower case
    String l = s.toLowerCase();

    // scan entire string
    int len = l.length();
    boolean letter = false;
    String ns = "";
    for (int i=0; i<len; i++) {
      if (!letter && i < len - 1 && l.substring(i, i+2).equals("d(")) {
        // convert d(x)/d(y) notation to standard derive(x, y) notation
        i += 2;
        int s1 = i;
        for (int paren=1; paren>0; i++) {
          // check for correct syntax
          if (i >= len) return s;
          char c = l.charAt(i);
          if (c == '(') paren++;
          if (c == ')') paren--;
        }
        int e1 = i-1;
        // check for correct syntax
        if (i > len - 3 || !l.substring(i, i+3).equals("/d(")) return s;
        i += 3;
        int s2 = i;
        for (int paren=1; paren>0; i++) {
          // check for correct syntax
          if (i >= len) return s;
          char c = l.charAt(i);
          if (c == '(') paren++;
          if (c == ')') paren--;
        }
        int e2 = i-1;
        ns = ns + "derive(" + s.substring(s1, e1) +
                        "," + s.substring(s2, e2) + ")";
        i--;
      }
      else if (!letter && i < len - 4 && l.substring(i, i+5).equals("link(")) {
        // evaluate link(code) notation and replace with link variable
        i += 5;
        int s1 = i;
        try {
          while (l.charAt(i) != '(') i++;
        }
        catch (ArrayIndexOutOfBoundsException exc) {
          // incorrect syntax
          return s;
        }
        i++;
        int e1 = i-1;
        int s2 = i;
        for (int paren=2; paren>1; i++) {
          // check for correct syntax
          if (i >= len) return s;
          char c = l.charAt(i);
          if (c == '(') paren++;
          if (c == ')') paren--;
        }
        int e2 = i-1;
        // check for correct syntax
        if (i >= len || l.charAt(i) != ')') return s;
        String[] strs = new String[1];
        strs[0] = s.substring(s1, e1) + "(";

        // parse method's arguments; determine if they are Data or RealType
        String sub = s.substring(s2, e2);
        StringTokenizer st = new StringTokenizer(sub, ",", false);
        boolean first = true;
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          if (first) first = false;
          else strs[0] = strs[0] + ",";
          RealType rt = RealType.getRealTypeByName(token);
          strs[0] = strs[0] + (rt == null ? "visad.Data"
                                          : "visad.RealType");
        }
        strs[0] = strs[0] + ")";

        // obtain Method object and store it in a link variable
        Method[] meths = FormulaUtil.stringsToMethods(strs);
        String link = "link" + (++linkNum);
        // make sure linked method actually exists
        if (meths[0] == null) return s;
        try {
          fm.setThing(link, new VMethod(meths[0]));
        }
        // catch any errors setting the link variable
        catch (FormulaException exc) {
          return s;
        }
        catch (VisADException exc) {
          return s;
        }
        catch (RemoteException exc) {
          return s;
        }
        ns = ns + "linkx(" + link + "," + s.substring(s2, e2) + ")";
      }
      else if (!letter) {
        int j = i;
        char c = l.charAt(j++);
        while (j < len && ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))) {
          c = l.charAt(j++);
        }
        // check for end-of-string
        if (j == len) return ns + s.substring(i, len);
        if (c == '[') {
          // convert x[y] notation to standard getSample(x, y) notation
          int k = j;
          for (int paren=1; paren>0; k++) {
            // check for correct syntax
            if (k >= len) return s;
            c = l.charAt(k);
            if (c == '[') paren++;
            if (c == ']') paren--;
          }
          ns = ns + "getSample(" + s.substring(i, j-1) +
                             "," + s.substring(j, k-1) + ")";
          i = k-1;
        }
        else ns = ns + s.charAt(i);
      }
      else {
        // append character to new string
        ns = ns + s.charAt(i);
      }
      char c = (i < len) ? l.charAt(i) : '\0';
      letter = (c >= 'a' && c <= 'z');
    }
    return ns;
  }

  // useful methods for advanced reflection:

  /** convert an array of strings of the form
      &quot;package.Class.method(Class, Class, ...)&quot;
      to an array of Method objects */
  public static Method[] stringsToMethods(String[] strings) {
    int len = strings.length;
    Method[] methods = new Method[len];
    for (int j=0; j<len; j++) {
      // remove spaces
      StringTokenizer t = new StringTokenizer(strings[j], " ", false);
      String s = "";
      while (t.hasMoreTokens()) s = s + t.nextToken();

      // separate into two strings
      t = new StringTokenizer(s, "(", false);
      String pre = t.nextToken();
      String post = t.nextToken();

      // separate first string into class and method strings
      t = new StringTokenizer(pre, ".", false);
      String c = t.nextToken();
      int count = t.countTokens();
      for (int i=0; i<count-1; i++) c = c + "." + t.nextToken();
      String m = t.nextToken();

      // get argument array of strings
      t = new StringTokenizer(post, ",)", false);
      count = t.countTokens();
      String[] a;
      if (count == 0) a = null;
      else a = new String[count];
      int x = 0;
      while (t.hasMoreTokens()) a[x++] = t.nextToken();

      // convert result to Method object
      Class clas = null;
      try {
        clas = Class.forName(c);
      }
      catch (ClassNotFoundException exc) {
        if (FormulaVar.DEBUG) {
          System.out.println("ERROR: Class c does not exist!");
        }
        methods[j] = null;
        continue;
      }
      Class[] param;
      if (a == null) param = null;
      else param = new Class[a.length];
      for (int i=0; i<count; i++) {
        // hack to convert array arguments to correct form
        if (a[i].endsWith("[]")) {
          a[i] = "[L" + a[i].substring(0, a[i].length()-2);
          while (a[i].endsWith("[]")) {
            a[i] = "[" + a[i].substring(0, a[i].length()-2);
          }
          a[i] = a[i] + ";";
        }

        try {
          param[i] = Class.forName(a[i]);
        }
        catch (ClassNotFoundException exc) {
          if (FormulaVar.DEBUG) {
            System.out.println("ERROR: Class a[i] does not exist!");
          }
          methods[j] = null;
          continue;
        }
      }
      Method method = null;
      try {
        method = clas.getMethod(m, param);
      }
      catch (NoSuchMethodException exc) {
        if (FormulaVar.DEBUG) {
          System.out.println("ERROR: Method m does not exist!");
        }
        methods[j] = null;
        continue;
      }
      methods[j] = method;
    }
    return methods;
  }

  /** attempt to invoke a Method with the given Object arguments, performing
      static method auto-detection and automatic array compression */
  public static Thing invokeMethod(Method m, Object[] o)
                                   throws IllegalAccessException,
                                          IllegalArgumentException,
                                          InvocationTargetException {
    Object obj;
    Object[] args;
    Class[] c = m.getParameterTypes();
    int num = (o == null) ? 0 : o.length;
    int len = -1;
    int a = -1;
    if (c != null) {
      len = c.length;
      for (int i=0; i<len; i++) {
        if (c[i].isArray()) a = i;
      }
    }
    if (Modifier.isStatic(m.getModifiers())) {
      // static method
      obj = null;
      if (num > 0) {
        if (a < 0) {
          args = new Object[num];
          System.arraycopy(o, 0, args, 0, num);
        }
        else {
          // compress some of the arguments into array form
          args = new Object[len];
          if (a > 0) System.arraycopy(o, 0, args, 0, a);
          Object array = Array.newInstance(c[a].getComponentType(), num-len+1);
          System.arraycopy(o, a, array, 0, num-len+1);
          args[a] = array;
          if (a < len-1) System.arraycopy(o, num-len+a+1, args, a+1, len-a-1);
        }
      }
      else args = null;
    }
    else {
      // object method
      if (num > 0) obj = o[0];
      else obj = null;
      if (num > 1) {
        if (a < 0) {
          args = new Object[num-1];
          System.arraycopy(o, 1, args, 0, num-1);
        }
        else {
          // compress some of the arguments into array form
          args = new Object[len];
          if (a > 0) System.arraycopy(o, 1, args, 0, a);
          Object array = Array.newInstance(c[a].getComponentType(), num-len);
          System.arraycopy(o, a+1, array, 0, num-len);
          args[a+1] = array;
          if (a < len-1) System.arraycopy(o, num-len+a+1, args, a+1, len-a-1);
        }
      }
      else args = null;
    }
    Object ans = m.invoke(obj, args);
    return (ans instanceof Thing ? (Thing) ans : null);
  }

}

