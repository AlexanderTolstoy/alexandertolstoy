package DataStructure;

import OutputStructure.Mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * CheckModule
 * Created by ccwei on 2018/9/2.
 */
public class ResourcePool {

    private static String fileName;
    private static String filePath;
    private static Model model = new Model();
    private static HashMap<String,Item>  Items = new HashMap<String, Item>();
    private static HashMap<String,Entity>  Entities = new HashMap<String, Entity>();
    private static HashMap<String,Attribute>  Attributes = new HashMap<String, Attribute>();
    private static HashMap<String,Link>  Links = new HashMap<String, Link>();
    private static HashMap<String,HashMap<String,DiagramLink>>  diagramLinksMapping = new HashMap<String, HashMap<String,DiagramLink>>();
    private static HashMap<String, Diagram> diagrams = new HashMap<String, Diagram>();
    private static HashMap<String,ArrayList<Mapping>> diagramMappingMaps = new HashMap<String, ArrayList<Mapping>>();
    private static HashMap<String,Relationship> relationships = new HashMap<String,Relationship>();
    private static HashMap<String, HashSet<String>> hub2SatMap = new HashMap<String,HashSet<String>>();
    private static HashMap<String, HashMap<String,String>> typeHashMap = new HashMap<String, HashMap<String, String>>();


    public static HashMap<String, HashMap<String, String>> getTypeHashMap() {
        return typeHashMap;
    }

    public static void setTypeHashMap(HashMap<String, HashMap<String, String>> typeHashMap) {
        ResourcePool.typeHashMap = typeHashMap;
    }

    public static HashMap<String, HashSet<String>> getHub2SatMap() {
        return hub2SatMap;
    }
    public static void putHub2SatMap(String key, HashSet<String> set){
        hub2SatMap.put(key,set);
    }

    public static HashMap<String, Relationship> getRelationships() {
        return relationships;
    }
    public static void putRelationship(String key, Relationship rs){
        relationships.put(key,rs);
    }

    public static void putDiagramMappingMaps(String key, ArrayList<Mapping> mappings){
        diagramMappingMaps.put(key,mappings);
    }

    public static HashMap<String, ArrayList<Mapping>> getDiagramMappingMaps() {
        return diagramMappingMaps;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        ResourcePool.fileName = fileName;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        ResourcePool.filePath = filePath;
    }

    public static Model getModel() {
        return model;
    }

    public static void setModel(Model model) {
        ResourcePool.model = model;
    }

    public static HashMap<String, Diagram> getDiagram() {
        return diagrams;
    }

    public static HashMap<String, HashMap<String,DiagramLink>> getDiagramLinks() {
        return diagramLinksMapping;
    }

    public static HashMap<String, Link> getLinks() {
        return Links;
    }
    public static void putDiagrams(String key, Diagram value){
        diagrams.put(key,value);
    }

    public static void putLink(String key,Link link) {
        Links.put(key,link);
    }

    public static HashMap<String, Item> getItems() {
        return Items;
    }

    public static HashMap<String, Entity> getEntities() {
        return Entities;
    }

    public static HashMap<String, Attribute> getAttributes() {
        return Attributes;
    }

    public static void putItem(String key, Item value){
        Items.put(key,value);
    }

    public static Item getItem(String key){
        return Items.get(key);
    }

    public static void putEntity(String key,Entity value){
        Entities.put(key,value);
    }

    public static Entity getEntitiy(String key){
        return Entities.get(key);
    }

    public static void putAttribute(String key, Attribute value){
        Attributes.put(key,value);
    }

    public static Attribute getAttribute(String key){
        return  Attributes.get(key);
    }
}
