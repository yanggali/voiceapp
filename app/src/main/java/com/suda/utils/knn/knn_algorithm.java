package com.suda.utils.knn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YangJiali on 2016/11/20 0020.
 */
public class knn_algorithm {
    int[][] borrow_matrix;
    int[][] book_distance_matrix;
    //初始化借阅矩阵
    public void init_borrow(List<Borrow_record> records)
    {
        Map<String,Integer> Readers = new HashMap<>();
        Map<String,Integer> Books = new HashMap<>();
        int reader_l = 0, book_l = 0;
        //获取到所有的读者长度和书籍长度
        for (int i = 0;i < records.size();i++)
        {
            if (Readers.get(records.get(i).reader_id)==null)
                Readers.put(records.get(i).reader_id, reader_l++);
            if (!Books.containsKey(records.get(i).getBook_id()))
                Books.put(records.get(i).book_id,book_l++);
        }
        borrow_matrix = new int[book_l][reader_l];
        int b,r;
        for (int i = 0;i < records.size();i++)
        {
            b = Books.get(records.get(i).book_id);
            r = Readers.get(records.get(i).reader_id);
            borrow_matrix[b][r] = 1;
        }
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
}
