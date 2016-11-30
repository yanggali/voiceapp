package com.suda.utils.knn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by YangJiali on 2016/11/20 0020.
 * knn 算法
 */
/**
 * Created by YangJiali on 2016/11/20 0020.
 */
public class knn_algorithm {
    //int[][] borrow_matrix;
    //int[][] book_distance_matrix;
    Map<String,Integer> Readers = new HashMap<>();
    Map<String,Integer> Books = new HashMap<>();

    public Map<String, Integer> getReaders() {
        return Readers;
    }

    public Map<String, Integer> getBooks() {
        return Books;
    }

    //初始化借阅矩阵
    public int[][] init_borrow(List<Borrow_record> records)
    {
        //Readers = new HashMap<>();
        //Books = new HashMap<>();
        int reader_l = 0, book_l = 0;
        //获取到所有的读者长度和书籍长度
        for (int i = 0;i < records.size();i++)
        {
            if (Readers.get(records.get(i).reader_id)==null)
                Readers.put(records.get(i).reader_id, reader_l++);
            if (!Books.containsKey(records.get(i).getBook_id()))
                Books.put(records.get(i).book_id,book_l++);
        }
        int[][] borrow_matrix = new int[book_l][reader_l];
        int b,r;
        for (int i = 0;i < records.size();i++)
        {
            b = Books.get(records.get(i).book_id);
            r = Readers.get(records.get(i).reader_id);
            borrow_matrix[b][r] = 1;
        }
        return borrow_matrix;
    }

    public int[][] disMatrix(int[][] matrix)
    {
        int size = matrix.length;
        int[][] book_distance_matrix = new int[size][size];
        for (int i = 0;i < size;i++)
        {
            for (int j = 0;j < size;j++)
            {
                book_distance_matrix[i][j] = distance(matrix[i],matrix[j]);
            }
        }
        return book_distance_matrix;
    }
    public void printMatrix(int[][] matrix)
    {
        for (int i = 0;i < matrix.length;i++)
        {
            for (int j = 0;j < matrix[i].length;j++)
            {
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }
    }

    public int distance(int[] a,int[] b)
    {
        int dis = 0;
        for (int i = 0;i < a.length;i++)
        {
            int temp = (a[i] - b[i])*(a[i] - b[i]);
            dis += temp;
        }
        return dis;
    }
    public int[] sort(int[] array)
    {
        int size = array.length;
        int[] temp = array,itemsnum = new int[size];
        for (int i = 0;i < size;i++)
        {
            itemsnum[i] = i;
        }
        for (int i = 0;i < size;i++)
        {
            for (int j = i+1;j < size;j++)
            {
                if (temp[i] > temp[j])
                {
//                    int t = temp[i];
//                    temp[i] = temp[j];
//                    temp[j] = t;
                    int tt = itemsnum[i];
                    itemsnum[i] = itemsnum[j];
                    itemsnum[j] = tt;
                }
            }
        }
        return itemsnum;
    }
    //求出每本书的包括它自己的k近邻
    public String[][] k_neighbours(int[][] dismatrix,int k)
    {
        int book_size = dismatrix.length;
        String[][] k_neighbours = new String[book_size][k];
        for (int i = 0;i < book_size;i++)
        {
            int[] temp = sort(dismatrix[i]);
            for (int j = 0;j < k;j++)
            {
                k_neighbours[i][j] = getBid(temp[j]);
            }
        }
        return k_neighbours;
    }
    //找到下标对应的id
    public String getBid(int num) {
        Iterator it = Books.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(num)) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public void printMatrix(String[][] matrix)
    {
        for (int i = 0;i < matrix.length;i++)
        {
            for (int j = 0;j < matrix[i].length;j++)
            {
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }
    }
}
