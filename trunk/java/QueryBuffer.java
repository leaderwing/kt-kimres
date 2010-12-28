package test;

import java.io.Serializable;
import java.lang.Exception;


class ItemType implements Serializable {

    public String value = "";
    public String className = "";
    public String classType = ""; // IE, UE, RW, CONJ, QTA, SQTA, QLA, SQLA
    public String ID = "";
    public String name = "";
    public String variable = "";
    public long start = 0;
    public long end = 0;
    public String progreg = ""; // module regcognize this entity GATE (G) or
    // KIM(K)
    public String relation = ""; // relation collection
    public int direction = 1; // 1: left->right 2: right->left
    public int relcount = 0; //relation only
    public int violation = 0;
    public String var = "";
    public String quantifier;
    public String wordfollow = "";
    public String wordbefore = "";
    public boolean delete = false;
    public boolean valueOfNormalQTA = false; //use for obj of a relation identified from quantitative adj


    public int subindex = 0; // use for relation only
    public int objindex = 0; // use for relation only
    private boolean identifiedFromQTA = false; //use for classType = RW (relation) only
    private boolean identifiedFromSQTA = false; //use for relation only


    public int row = 0; // use for drawing
    public int col = 0; // use for drawing
    public int totalrel = 0; // use for drawing
    public int left = 0; // use for drawing
    public int top = 0; // use for drawing
    public int width = 0; // use for drawing
    public int height = 0; // use for drawing
    public String text = ""; // use for drawing;
    public boolean isUsed = false;

    //New added for RemoveTriple()
    public ItemType preNearestUE = null; //Entity only
    
    // Mark entity that connected to in case MAX MIN relation.
    private boolean isMarkMaxMinRel = false;
    private boolean isTmpRel = false;
    private boolean isNotEntity = false;


    public boolean isValueOfNormalQTA() {
        return valueOfNormalQTA;
    }

    public void setValueOfNormalQTA(boolean valueOfNormalQTA) {
        this.valueOfNormalQTA = valueOfNormalQTA;
    }

    public boolean isIdentifiedFromSQTA() {
        return identifiedFromSQTA;
    }

    public void setIdentifiedFromSQTA(boolean isIdentifiedFromSQTA) {
        this.identifiedFromSQTA = isIdentifiedFromSQTA;
    }

    public boolean isIdentifiedFromQTA() {
        return identifiedFromQTA;
    }

    public void setIdentifiedFromQTA(boolean isIdentifiedFromAdj) {
        this.identifiedFromQTA = isIdentifiedFromAdj;
    }

    public boolean isNotEntity() {
        return isNotEntity;
    }

    public void setNotEntity(boolean isNotEntity) {
        this.isNotEntity = isNotEntity;
    }

    public boolean isTmpRel() {
        return isTmpRel;
    }

    public void setTmpRel(boolean isTmpRel) {
        this.isTmpRel = isTmpRel;
    }

    public boolean isMarkMaxMinRel() {
        return isMarkMaxMinRel;
    }

    public void setMarkMaxMinRel(boolean isMarkMaxMinRel) {
        this.isMarkMaxMinRel = isMarkMaxMinRel;
    }

    public void InsertRelation(String rel, int dir, TripleType querytriple)
            throws Exception {
        if (CheckRelationConstraint.checkValidRelation(rel, dir, querytriple) == false) {
            violation = 1;
            return;
        }
        if (relcount == 0) {
            relation += rel;
            direction = dir;
            relcount++;
        } else {
            String[] relcol = relation.split(",");
            boolean found = false;
            for (int i = 0; i < relcol.length; i++) {
                if (relcol[i].compareToIgnoreCase(rel) == 0) {
                    found = true;
                }
            }
            if (!found) {
                relation += "," + rel;
                direction = dir;
                relcount++;
            }
        }
    }

/*
    public ItemType clone() {
        ItemType tnew = new ItemType();
        tnew.ID = this.ID;
        tnew.className = this.className;
        tnew.classType = this.classType;
        tnew.start = this.start;
        tnew.end = this.end;
        tnew.value = this.value;
        tnew.wordbefore = this.wordbefore;
        tnew.wordfollow = this.wordfollow;
        tnew.name = this.name;
        tnew.progreg = this.progreg;
        tnew.col = this.col;
        tnew.delete = this.delete;
        tnew.direction = this.direction;
        tnew.variable = this.variable;
        tnew.quantifier = this.quantifier;
        tnew.subindex = this.subindex;
        tnew.violation = this.violation;
        return tnew;
    }*/
}
public class QueryBuffer implements Serializable {

    public static int MaxItem = 100;
    public String wordlist[];
    public String stemlist[];
    public int length = 0;
    public String query;
    public int totalrow = 0;
    public int totalcol = 0;
    public ItemType[] buffer;
    public static int unrecognized = 0;
    public static int invalidquery = 0;
    public static int recognized = 0;
    public static int querycount = 1;
    public static int nestedindex = -1;
    public static int linkedrel = -1;
    private RelationType relToNextAtomQuery = null;
    private boolean containSuperlativeQTA = false;
    private boolean containQTA = false;
    private boolean containCQTA = false;


    public QueryBuffer() {
        buffer = new ItemType[MaxItem];
    }

    public boolean isContainQTA() {
        return containQTA;
    }

    public void setContainQTA(boolean containQTA) {
        this.containQTA = containQTA;
    }

    public boolean isContainSuperlativeQTA() {
        return containSuperlativeQTA;
    }

    public void setContainSuperlativeQTA(boolean containSuperlativeQTA) {
        this.containSuperlativeQTA = containSuperlativeQTA;
    }


    public boolean isContainCQTA() {
        return containCQTA;
    }

    public void setContainCQTA(boolean containCQTA) {
        this.containCQTA = containCQTA;
    }

    public ItemType InsertItemOrg(String value, String className, String classType, long start, long end, String progreg, String wordfollow, String wordbefore) throws Exception {
        if (length == MaxItem) //maximum item limit
        {
            return null;
        }
        ItemType tmp = new ItemType();
        tmp.value = value;
        tmp.className = className;
        tmp.classType = classType;
        tmp.start = start;
        tmp.end = end;
        tmp.progreg = progreg;
        tmp.wordfollow = wordfollow;
        tmp.wordbefore = wordbefore;
        buffer[length] = tmp;
        length++;
        return tmp;
    }

    public ItemType InsertItem(String value, String className, String classType, long start, long end, String progreg, String wordfollow, String wordbefore) throws Exception {
        if (length == MaxItem) //maximum item limit
        {
            return null;
        }
        ItemType tmp = new ItemType();
        tmp.value = value;
        tmp.className = className;
        tmp.classType = classType;
        tmp.start = start;
        tmp.end = end;
        tmp.progreg = progreg;
        tmp.wordfollow = wordfollow;
        tmp.wordbefore = wordbefore;

        //check to find position to insert
        int index = CheckItemType(start, end, className);
        if (value.equalsIgnoreCase("fakeRW") || (index == -1)) {
            buffer[length] = tmp;
            length++;
            SortBuffer();
        } else if (index != -2) {
            buffer[index] = tmp; // replace
        }
        return tmp;
    }

    public ItemType InsertIE(String name, String value, String className, String classType, String ID, long start, long end, String progreg, String wordfollow, String wordbefore) throws Exception {
        if (length == MaxItem) //maximum item limit
        {
            return null;
        }
        ItemType tmp = new ItemType();
        tmp.value = value;
        tmp.name = name;
        tmp.className = className;
        tmp.classType = classType;
        tmp.start = start;
        tmp.end = end;
        tmp.progreg = progreg;
        tmp.ID = ID;
        tmp.wordfollow = wordfollow;
        tmp.wordbefore = wordbefore;

        //check to find position to insert
        int index = CheckItemType(start, end, classType);
        if (index == -1) {
            buffer[length] = tmp;
            length++;
            SortBuffer();
        } else if (index != -2) {
            buffer[index] = tmp; // replace
        }
        return tmp;
    }

    public ItemType InsertAfterItem(int afterindex, String value, String className, String classType) throws Exception {
        if (length == MaxItem) //maximum item limit
        {
            return null;
        }
        ItemType tmp = new ItemType();
        tmp.value = value;
        tmp.className = className;
        tmp.classType = classType;

        //check to find position to insert
        for (int i = length; i >= afterindex; i--) {
            buffer[i + 1] = buffer[i];
        }
        buffer[afterindex + 1] = tmp;
        length++;
        return tmp;
    }

    public ItemType InsertBeforeItem(int afterindex, String value, String className, String classType) throws Exception {
        if (length == MaxItem) //maximum item limit
        {
            return null;
        }
        ItemType tmp = new ItemType();
        tmp.value = value;
        tmp.className = className;
        tmp.classType = classType;

        //check to find position to insert
        for (int i = length; i >= afterindex; i--) {
            buffer[i + 1] = buffer[i];
        }
        buffer[afterindex] = tmp;
        length++;
        return tmp;
    }

    public String getParsedQuery() {
        String output = "";
        for (int i = 0; i < length; i++) {
            output += buffer[i].classType + " ";
        }

        return output;
    }

    public int CheckItemType(long start, long end, String ctype) throws Exception {
        for (int i = 0; i < length; i++) {
            if ((start >= buffer[i].start) && (end <= buffer[i].end)) {
                if (start == buffer[i].start && end == buffer[i].end) {
                    if ((ctype.substring(0, 2).compareTo("UE") == 0) || (ctype.compareTo("RW") == 0)) {
                        if (buffer[i].progreg.compareToIgnoreCase("G") == 0) {
                            return i;
                        } else {
                            return -2;
                        }
                    } else {
                        return -2;
                    }
                } else {
                    return -2; // new element is part of old element
                }
            }
            if ((((start < buffer[i].start) && (end >= buffer[i].end)) ||
                    ((start <= buffer[i].start) && (end > buffer[i].end)))) {
                if (buffer[i].progreg.compareToIgnoreCase("G") == 0) {
                    return i; // new element longer old element
                } else {
                    return -2;
                }
            }
        }
        return -1;
    }

    public void SortBuffer() {
        ItemType tmp;
        for (int i = 0; i < length - 1; i++) {
            for (int j = i + 1; j < length; j++) {
                if (buffer[i].start > buffer[j].start) {
                    tmp = buffer[i];
                    buffer[i] = buffer[j];
                    buffer[j] = tmp;
                }
            }
        }
    }

    public ItemType getItem(int index) {
        if (index < 0) {
            return null;
        }
        if (index > length) {
            return null;
        }
        return buffer[index];
    }

    public void setItem(int orgindex, int destindex) {
        if (orgindex > length || destindex > length) {
            return;
        }
        buffer[orgindex] = buffer[destindex];
    }

    public String getQuery() {
        return this.query;
    }

    public void Reset() {
        length = 0;
    }

    public String getmiddlestring(int subjindex, int objindex) {
        String result = "";

        int startindex = query.indexOf(buffer[subjindex].value) + buffer[subjindex].value.length();
        int endindex = query.indexOf(buffer[objindex].value);
        result = query.substring(startindex, endindex);

        return result;
    }

    public RelationType getRelToNextAtomQuery() {
        return relToNextAtomQuery;
    }

    public void setRelToNextAtomQuery(RelationType relToNextAtomQuery) {
        this.relToNextAtomQuery = relToNextAtomQuery;
    }

    public String test() {
        String str = query + "<br/>";
        for (int j = 0; j < this.length; j++) {
            ItemType item = this.getItem(j);
            str += item.ID + " " + item.classType + " " + item.className + " " + item.value + " " + item.wordbefore + " " + item.wordfollow + "<br/>";
            str += item.start + " " + item.end + " " + item.progreg + " " + item.relation + " " + item.direction + " " + item.relcount + " " + item.violation + "<br/><br/>";
        }

        return str + this.length;
    }
}