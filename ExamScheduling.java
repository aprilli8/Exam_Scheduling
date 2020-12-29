//I am the sole author of the work in this repository.

import structure5.*;
import java.util.Iterator;
import java.util.Scanner;

/**
 * This class creates a graph of student courses that are connected only if
 * at least one student is taking both of those classes. It then produces an
 * efficient exam schedule that so that no student has exams at the same time.
 * Extension 1: Final exam schedule is printed in alphabetical order.
 * Extension 2: Exam schedule for each student is printed in alphabetical order.
 */

public class ExamScheduling{

  //graph of all courses taken by inputed students
  protected GraphListUndirected<String, Integer> classes;

  //vector of singly linked lists that represent the exams in each time slot
  protected Vector<Vector<String>> examSlots;

  //vector of associations that holds all students' names and classes
  protected Vector<Association<String, String[]>> students;

  //Constructor creates new ExamScheduling object and instantiates classes, examSlots, and students
  public ExamScheduling(){
    classes = new GraphListUndirected<String, Integer>();
    examSlots = new Vector<Vector<String>>();
    students = new Vector<Association<String, String[]>>();
  }

  //pre: name, class1, class2, class3, and class4 are all valid strings
  //post: creates an array of a student's classes and passes it to addStudentClasses() method;
  //    also inserts array alphabetically into students vector based on name
  public void createStudent(String name, String class1, String class2, String class3, String class4){
    String[] studentClasses = new String[]{class1, class2, class3, class4};
    int i;
    for(i = 0; i < students.size(); i++){
      if(name.compareTo(students.get(i).getKey()) < 0){
        break;
      }
    }
    students.insertElementAt(new Association<String, String[]>(name, studentClasses), i);
    addStudentClasses(studentClasses);
  }

  //pre: student is an array
  //post: all student classes and corresponding edges are added to graph
  public void addStudentClasses(String student[]){
    //adds classes as vertices if they do not exist
    for(int i = 0; i < student.length; i++){
      if(!classes.contains(student[i])){
        classes.add(student[i]);
      }
    }
    //adds edges between classes if they do not exist, else increasing weight of edge by 1
    for(int i = 0; i < student.length-1; i++){
      for(int j = i+1; j < student.length; j++){
        if(!classes.containsEdge(student[i], student[j])){
          classes.addEdge(student[i], student[j], 1);
        }
        else{
          int currentEdge = classes.getEdge(student[i], student[j]).label();
          classes.getEdge(student[i], student[j]).setLabel(++currentEdge);
        }
      }
    }
  }

  //pre: none
  //post: creates exam slots so that no student has exams at the same time and the
  //      slots cannot be condensed any further without violating the first part
  public void fillSlots(){
    //runs as long as there are classes left in the graph
    while(!classes.isEmpty()){
      Iterator<String> iter = classes.iterator();
      Vector<String> currentSlot = new Vector<String>();
      currentSlot.add(iter.next());
      //runs for all classes in the graph
      while(iter.hasNext()){
        String current = iter.next();
        //if current class is a neighbor of any classes in the current exam slot,
        //marks the class as visited
        for(int i = 0; i < currentSlot.size(); i++){
          if(classes.containsEdge(current, currentSlot.get(i))){
            classes.visit(current);
          }
        }
        //for all classes that are not visited (not connected to any other classes
        //in the slot), adds it to currentSlot in alphabetical order
        if(!classes.visit(current)){
          int i;
          for(i = 0; i < currentSlot.size(); i++){
            if(current.compareTo(currentSlot.get(i)) < 0){
              break;
            }
          }
          currentSlot.add(i, current);
        }
      }
      //removes all added classes from the graph, adds slot to exam vector,
      //and resets the visited flag for all classes
      for(int i = 0; i < currentSlot.size(); i++){
        classes.remove(currentSlot.get(i));
      }
      examSlots.add(currentSlot);
      classes.reset();
    }
  }

  //Extension 1
  //pre: none
  //post: prints out an exam schedule for all classes
  public void printExamSlots(){
    System.out.println("Exam Schedule");
    for(int i = 0; i < examSlots.size(); i++){
      System.out.print("Slot " + (i+1) + ": ");
      for(int j = 0; j < examSlots.get(i).size()-1; j++){
        System.out.print(examSlots.get(i).get(j) + ", ");
      }
      System.out.println(examSlots.get(i).get(examSlots.get(i).size()-1));
    }
  }

  //Extension 2
  //pre: none
  //post: prints out an exam schedule for each student, listed in alphabetical order
  public void printStudentExamSlots(){
    //finds appropriate exam slots for every class of every student
    for(int i = 0; i < students.size(); i++){
      String[] studentExams = students.get(i).getValue();
      for(int j = 0; j < studentExams.length; j++){
        for(int k = 0; k < examSlots.size(); k++){
          for(int l = 0; l < examSlots.get(k).size(); l++){
            if(studentExams[j].equals(examSlots.get(k).get(l))){
              students.get(i).getValue()[j] = "Slot " + (k+1) + ": " + studentExams[j];
            }
          }
        }
      }
    }
    //prints out the student name and their exam schedule
    System.out.println("Student Exam Schedules");
    for(int i = 0; i < students.size(); i++){
      System.out.println("- Student: " + students.get(i).getKey() + " - ");
      for(int j = 0; j < students.get(i).getValue().length; j++){
        System.out.println(students.get(i).getValue()[j]);
      }
    }
  }

  //main method that takes in user input, creates exam slots, and prints out schedules
  public static void main(String[] args){
    Scanner scan = new Scanner(System.in);
    ExamScheduling finals = new ExamScheduling();
    while(scan.hasNext()){
      finals.createStudent(scan.nextLine(), scan.nextLine(), scan.nextLine(), scan.nextLine(), scan.nextLine());
    }
    finals.fillSlots();
    finals.printExamSlots();
    System.out.println("");
    finals.printStudentExamSlots();
  }

}
