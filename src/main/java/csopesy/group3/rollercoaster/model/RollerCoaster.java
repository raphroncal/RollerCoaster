package csopesy.group3.rollercoaster.model;

import java.util.concurrent.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class RollerCoaster {

    public final int nPassengers;
    public final int nCars;
    public final int nPassengersPerCar;

    static Semaphore bq = new Semaphore(0); //board queue
    static Semaphore ubq = new Semaphore(0); //unboard queue

    static Semaphore bq_count = new Semaphore(1); //board queue count
    static Semaphore ubq_count = new Semaphore(1); //unboard queue count

    static Semaphore all_ab = new Semaphore(0); //All Aboard
    static Semaphore all_ash = new Semaphore(0); //All Ashore

    static ArrayList<Semaphore> load_area = new ArrayList<>();
    static ArrayList<Semaphore> unload_area = new ArrayList<>();

    static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SS");

    static int RemainingPassengers;

    public RollerCoaster(int nPassengers, int nCars, int nPassengersPerCar) {
        this.nPassengers = nPassengers;
        this.nCars = nCars;
        this.nPassengersPerCar = nPassengersPerCar;
    }

    public void initialize() {
        System.out.println(nPassengers);
        System.out.println(nCars);
        System.out.println(nPassengersPerCar);

        // Set RemainingPassengers
        RemainingPassengers = nPassengers;

        // Initialize loading/unloading area semaphores
        for (int i = 0; i < nCars; i++) {
            load_area.add(new Semaphore(0));
            unload_area.add(new Semaphore(0));
        }

        load_area.get(0).release();
        unload_area.get(0).release();

        Passenger[] p = new Passenger[nPassengers];
        Thread[] pThread = new Thread[nPassengers];

        Action action = new Action(nPassengers, nCars, nPassengersPerCar);

        for (int i = 0; i < nPassengers; i++)
            p[i] = new Passenger(action, i, nCars);

        for (int i = 0; i < nPassengers; i++)
            pThread[i] = new Thread(p[i]);

        for (int i = 0; i < nPassengers; i++)
            pThread[i].start();

        Car[] c = new Car[nCars];
        Thread[] cThread = new Thread[nCars];

        for (int i = 0; i < nCars; i++)
            c[i] = new Car(action, nPassengersPerCar, i, nCars);

        for (int i = 0; i < nCars; i++)
            cThread[i] = new Thread(c[i]);

        for (int i = 0; i < nCars; i++)
            cThread[i].start();

        try {
            for (int i = 0; i < nPassengers; i++)
                pThread[i].join();
        } catch (InterruptedException ignored) {}


        try {
            for (int i = 0; i < nCars; i++)
                cThread[i].join();
        } catch (InterruptedException e) {}

        System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"All rides completed.");
        System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"The park is now closed.");
    }

    // Car class
    static class Car implements Runnable{
        Action action; //Rollercoaster monitor
        int C; //capacity
        int i; //car id
        int m; //car count

        public Car(Action action, int C, int i, int m){
            this.action = action;
            this.C = C;
            this.i = i;
            this.m = m;
        }

        private int next(int i) {
            return (i + 1) % m;
        }

        public void run(){
            while (RemainingPassengers >= C) {
                try {
                    load_area.get(this.i).acquire();
                    System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Car "+i+" is loading.");
                    action.setCurrent(this.i);
                    action.load(this.i);
                    RemainingPassengers -= C;
                } catch (InterruptedException e) {}

                try{
                    System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Car "+this.i+" is running.");
                    load_area.get(next(this.i)).release();
                    Thread.sleep(10000); // Run for 10 seconds (Fixed time)
                } catch (InterruptedException e) {}
                try {
                    unload_area.get(this.i).acquire();
                    System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Car "+i+" is unloading.");
                    action.unload(this.i);
                    unload_area.get(next(this.i)).release();
                } catch (InterruptedException e) {}
            }

            System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: Not enough passengers left, Car "+this.i+" has stopped.");
        }

    }

    // Rollercoaster Monitor
    static class Action {
        int currentCar;
        int passengers;
        int capacity;
        int cars;

        public Action(int n, int m, int C){
            this.passengers = n;
            this.capacity = C;
            this.cars = m;
        }

        void load(int i){
            bq.release(this.capacity); //signals that passengers can board

            try{
                all_ab.acquire(); //wait for car to be full
                System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"All aboard Car "+i+".");
            } catch (InterruptedException E){}
        }

        void unload(int i){
            ubq.release(this.capacity); //signals that boarded passengers can unboard
            try{
                all_ash.acquire(); //wait for car to be empty

                System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"All ashore Car "+i+".");
            } catch (InterruptedException E){}
            this.passengers -= this.capacity;

            if (passengers < capacity) {
                bq.release();
            }
        }

        void board(int i, int car){
            System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Passenger "+ i +" has boarded car "+car+".");
        }

        void unboard(int i, int car) {
            System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Passenger "+ i +" has unboarded car "+car+".");
        }

        void setCurrent(int i) {
            this.currentCar = i;
        }

        int getCurrent() {
            return this.currentCar;
        }

    }

    // Passenger Class
    static class Passenger implements Runnable {
        int i;
        int capacity;
        int currentCar;
        static int boarders = 0;
        static int unboarders = 0;
        Action action;

        public Passenger(Action action, int i, int C) {
            this.i = i;
            this.action = action;
            this.capacity = C;
        }

        @Override
        public void run() {

            try{
                System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Passenger "+this.i+" is roaming the park...");
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10000)); // Roam for 5 - 10 seconds
            } catch (InterruptedException e) {}

            System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Passenger "+ i +" is waiting to board the ride.");

            try{
                bq.acquire();
                if (!(RemainingPassengers < capacity)) {
                    this.currentCar = action.getCurrent();
                    bq_count.acquire();
                    action.board(this.i, this.currentCar);
                    boarders++;
                    System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"There are "+boarders+" passengers aboard.");
                    if(boarders == capacity){ // checks if car is full
                        all_ab.release(); // signals that is full, thus all aboard
                        boarders = 0;
                    }
                    bq_count.release();
                    ubq.acquire(); //fall into unboarding queue
                    ubq_count.acquire();
                    action.unboard(this.i, this.currentCar);
                    unboarders++;
                    System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"There are "+(capacity-unboarders)+" passengers left aboard.");
                    if(unboarders == capacity){ // checks if car is empty
                        all_ash.release(); // signals that is empty, thus all ashore
                        unboarders = 0;
                    }
                    ubq_count.release();
                } else {
                    System.out.println("["+formatter.format(Calendar.getInstance().getTime())+"]: "+"Passenger "+this.i+" cannot ride anymore and has left the park...");
                    bq.release();
                }
            } catch (InterruptedException E){}

        }

    }

}




