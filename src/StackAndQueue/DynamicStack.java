package StackAndQueue;

import java.util.Arrays;

public class DynamicStack extends CustomStack{

    public DynamicStack(){
        super();
    }

    public DynamicStack(int item){
        super(item);
    }

    @Override
    public boolean push(int item) {
        if(this.isFull()){
            //double the array size;
            int[] temp = new int[data.length * 2];

            //copy all previous items;
            for(int i = 0 ; i < data.length ; i ++){
                temp[i] = data[i];
            }

            data = temp;
        }
        //insert item
        return super.push(item);
    }
}
