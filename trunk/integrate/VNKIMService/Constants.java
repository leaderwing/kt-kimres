package org.me.VNKIMService;
/**
 * 
 * 
 *
 */
public class Constants {

	// Adjective type
	public static final String SUPERLATIVE_QUANTITATIVE_ADJ = "SQTA";
    public static final String COMPARATIVE_QUANTITATIVE_ADJ = "CQTA";
    public static final String SUPERLATIVE_QUANLITATIVE_ADJ = "SQLA";
	public static final String QUANTITATIVE_ADJ = "QTA";
	public static final String QUANLITATIVE_ADJ = "QLA";

    // Order Type
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";

    // Comparative Relations
    public static final String GREATER = "isGreaterThan";
    public static final String SMALLER = "isSmallerThan";


	// Query Type
	public static final String ALL = "ALL";
	public static final String INVALID = "INVALID";
	public static final String VALID = "VALID";

	// Struts constants
	public static final String SUCCESS_FORWARD = "success";

	// Session
	public static final String SESSION_RESULT_LIST = "SESSION_RESULT_LIST";
	public static final String SESSION_REPORT_RESULT = "SESSION_REPORT_RESULT";
	public static final String SESSION_SET_LANGUAGE = "SESSION_SET_LANGUAGE";
	public static final String SESSION_SUBMITED = "SESSION_SUBMITED";
	public static final String SESSION_LANGUAGE = "SESSION_LANGUAGE";

	// Parameter
	public static final String LANG_PARAMETER = "lang";

	// PROPERTIES
	public static final String GATE_HOME = "gate.home";
	public static final String GATE_PLUGIN_HOME = "gate.plugin.home";
	public static final String GATE_SITE_CONFIG_FILE_LOCATION = "gate.site.config.file.location";
	public static final String GATE_USER_CONFIG_FILE_LOCATION = "gate.user.config.file.location";
	public static final String ANNIE_LOCATION = "annie.location";
	public static final String LIST_DEF_LOCATION = "list.def.location";
	public static final String MAP_DEF_LOCATION = "map.def.location";
	public static final String IE_DIC_LOCATION = "ie.dic.location";
	public static final String UE_DIC_LOCATION = "ue.dic.location";
	public static final String CONJ_DIC_LOCATION = "conj.dic.location";
	public static final String RELATION_DIC_LOCATION = "relation.dic.location";
	public static final String MAX_MIN_DIC_LOCATION = "maxmin.dic.location";

	// Entity Type
	public static final String IE = "IE";
	public static final String UE = "UE";
	public static final String RW = "RW";
	public static final String CONJ = "CONJ";

    public static final String REAL_NUMBER = "REALNUM";

	public static final String UE_ = "UE_";
	public static final String UE_AGENT = "UE_Agent";
	public static final String UE_ENTITY = "UE_Entity";
	public static final String UE_ALIAS = "UE_Alias";
	public static final String UE_QUOTE = "UE_Quote";
	public static final String UE_COST = "UE_Cost";
	public static final String UE_PRESIDENT = "UE_President";
	public static final String UE_JUNK = "UE_JUNK";


	// Regcognize
	public static final String KIM_RECOGNIZE = "K";
	public static final String GATE_RECOGNIZE = "G";

	// Format of IEDIC.xml
	public static final String IEDIC_ENTRY_TAG = "entry";
	public static final String IEDIC_IEVALUE_TAG = "IEvalue";
	public static final String IEDIC_IECLASS_TAG = "IEclass";

	// Format of UEDIC.xml
	public static final String UEDIC_ENTRY_TAG = "entry";
	public static final String UEDIC_UEVALUE_TAG = "UEvalue";
	public static final String UEDIC_UECLASS_TAG = "UEclass";

	// Format of UEDIC.xml
	public static final String CONJDIC_ENTRY_TAG = "entry";
	public static final String CONJDIC_CONJVALUE_TAG = "CONJvalue";
	public static final String CONJDIC_CONJCLASS_TAG = "CONJclass";

	// Format of relation.xml
	public static final String RELATION_ENTRY_TAG = "entry";
	public static final String RELATION_SUBJVALUE_ATTR = "subjvalue";
	public static final String RELATION_RELVALUE_ATTR = "relvalue";
	public static final String RELATION_OBJVALUE_ATTR = "objvalue";
	public static final String RELATION_SUBJCLASS_ATTR = "subjclass";
	public static final String RELATION_OBJCLASS_ATTR = "objclass";
	public static final String RELATION_RELATION_ATTR = "relation";
	public static final String RELATION_DIR_ATTR = "dir";

	// Format of MaxMinDIC.xml
	public static final String MAX_MIN_ENTRY_TAG = "entry";
	public static final String MAX_MIN_ADJVALUE_ATTR = "ADJvalue";
	public static final String MAX_MIN_SUPADJTYPE_ATTR = "SupAdjType";

    //Format of OrderTypeDic.xml
    public static final String OT_ENTRY_TAG = "entry";
	public static final String OT_ADJ_VALUE_ATTR = "ADJvalue";
	public static final String OT_ORDER_TYPE_ATTR = "OrderType";

    //Format of ComparativeRelDic.xml
    public static final String CR_ENTRY_TAG = "entry";
	public static final String CR_ADJ_VALUE_ATTR = "ADJvalue";
	public static final String CR_REL_ATTR = "Rel";

    //Format of ExceptionRelDic.xml
    public static final String ER_ENTRY_TAG = "entry";
	public static final String ER_REL_VALUE_ATTR = "Relvalue";

	// For QueryBuffer
	public static final int IGNORE_POS = -2;
	public static final int CREATE_POS = -1;

	// RULES
	public static final String TRANSFORM_RULE_LOCATION = "rule.transform";
	public static final String TRANSFORM_RULE_FOR_ADJ_LOCATION = "rule.transformforADJ";
	public static final String TRANSFORM_RULE_FOR_CONJ_LOCATION = "rule.transformforCONJ";
	public static final String CONSTRAINTS_RULE_LOCATION = "rule.constraints";
	public static final String TRANSFORM_RULE_TAG = "rule";
	public static final String TRANSFORM_PRIORITY_TAG = "priority";
	public static final String TRANSFORM_PREMISE_TAG = "premise";
	public static final String TRANSFORM_PREMISELIST_TAG = "premiselist";
	public static final String TRANSFORM_CONSEQUENTLIST_TAG = "consequentlist";
	public static final String TRANSFORM_CONSEQUENT_TAG = "consequent";
	public static final String TRANSFORM_SUBJECT_TAG = "subject";
	public static final String TRANSFORM_RELATION_TAG = "relation";
	public static final String TRANSFORM_OBJECT_TAG = "object";
	public static final String TRANSFORM_CLASSNAME_ATTR = "className";
	public static final String TRANSFORM_VALUE_ATTR = "value";
	public static final String TRANSFORM_OBJCLASS_ATTR = "objclass";
	public static final String TRANSFORM_OBJVALUE_ATTR = "objvalue";

	public static final String TRANSFORM_SUBJCLASS_ATTR = "subjclass";
	public static final String TRANSFORM_SUBJVALUE_ATTR = "subjvalue";
	public static final String TRANSFORM_CLASSTYPE_ATTR = "classType";
	public static final String TRANSFORM_WORDFOLLOW_ATTR = "wordfollow";
	public static final String TRANSFORM_WORDBEFORE_ATTR = "wordbefore";
	public static final String TRANSFORM_VAR_ATTR = "var";
	public static final String TRANSFORM_QUANTIFIER_ATTR = "quantifier";
	public static final String TRANSFORM_DELETE_ATTR = "delete";
	public static final String TRANSFORM_ADJECTIVE_ATTR = "adjective";

	public static final String CONSTRAIN_RELATIONSHIP_TAG = "relationship";
	public static final String CONSTRAIN_PARENTCLASS_ATTR = "parentclass";
	public static final String CONSTRAIN_VALUE_ATTR = "value";
	public static final String CONSTRAIN_CONSTRAINT_TAG = "constraint";
	public static final String CONSTRAIN_OBJCLASS_ATTR = "objclass";
	public static final String CONSTRAIN_SUBJCLASS_ATTR = "subjclass";

	// QUALIFIER
	public static final String ASTERISK_SYMBOL = "*";
	public static final String QUESTION_SYMBOL = "?";

	//CG
	public static final int CG_MAGIN_LEFT = 5;
	public static final int CG_MAGIN_TOP = 5;
	public static final int CG_LEFT_COVER_BOX = 45;
	public static final int CG_TOP_COVER_BOX = 20;
	public static final int CG_BOTTOM_COVER_BOX = 20;
	public static final int CG_TOP_RELATION_AREA = 80;
	public static final int CG_COL_HEIGHT = 50;
	public static final int CG_ROW_WIDTH = 200;
	public static final int CG_TEXT_WIDTH = 10;
	public static final int CG_SIZE_OF_ARROW = 10;
	public static final int CG_ITEM_DISTANCE = 100; //Distance between 2 items

	public static final String HOW_MANY_STRING = "how many";
	public static final String HOW_MUCH_STRING = "how much";
	public static final String MOST_STRING = "most";
	public static final String LEAST_STRING = "least";
    public static final String MORE_STRING = "more";
    public static final String LESS_STRING = "less";
	public static final String AVERAGE_STRING = "average";
	public static final String QUOTE_STRING = "Quote";
	public static final String LANGUAGE_STRING = "Language";
	public static final String TIGER_STRING = "Tiger";

}
