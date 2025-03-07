package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//注意这个类叫Solution
@Disabled
class SudokuTest {
    @Test
    void testSimpleShuDu() {
        //直接在这里填写数独题目
        char[][] input = new char[][]{
            {'3', '2', '4', '.', '.', '.', '8', '7', '5'},
            {'1', '6', '7', '5', '3', '8', '.', '2', '.'},
            {'.', '.', '.', '7', '4', '2', '3', '1', '6'},
            {'.', '3', '.', '.', '7', '.', '.', '.', '.'},
            {'7', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '5', '6', '.', '3', '7'},
            {'2', '9', '8', '6', '1', '7', '5', '4', '.'},
            {'6', '.', '3', '2', '9', '5', '7', '8', '1'},
            {'5', '7', '1', '.', '.', '.', '.', '.', '.'},
        };
        //这里改成你的类名字
        new SimpleShuDu().solveSudoku(input);
        for (char[] chars : input) {
            System.out.println();
            for (char c : chars) {
                System.out.print(c + " ");
            }
        }
        Assertions.assertTrue(true);
    }

    static class LongShuDu {
        String inputFilePath;
        List<List<Object>> rowList;
        List<Set<Coordinate>> coll = new ArrayList<>();
        Set<Coordinate> addedSet = new HashSet<>();
        FileInputStream fis;
        Workbook workbook;
        Sheet sheet; // 获取第一个工作表
        private int[][] arr;


        public int[][] getArr() {
            return arr;
        }

        private HashSet[] rowSet = new HashSet[9];
        private HashSet[] colSet = new HashSet[9];
        private HashSet[] boxSet = new HashSet[9];

        {
            for (int i = 0; i < 9; i++) {
                rowSet[i] = new HashSet();
                colSet[i] = new HashSet();
                boxSet[i] = new HashSet();
            }

            ExcelReader reader = ExcelUtil.getReader(inputFilePath);
            rowList = reader.read();
            try {
                fis = new FileInputStream(inputFilePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                workbook = WorkbookFactory.create(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sheet = workbook.getSheetAt(0);
            try {

                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        byte[] rgb = getRGB(i, j);

                        Coordinate pos = new Coordinate(i, j);
                        if (addedSet.contains(pos)) {
                            continue;
                        }
                        addedSet.add(pos);
                        Set<Coordinate> set = new HashSet<>();
                        set.add(pos);
                        collNeighbor(pos, set, rgb);
                        coll.add(set);
                    }
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            LongShuDu shudu = new LongShuDu(new int[9][9]);

            long start = System.currentTimeMillis();
            System.out.println("开始时间：" + start);

            boolean handle = shudu.handle(0);

            long end = System.currentTimeMillis();
            System.out.println("结束时间" + end);
            System.out.println("时间差：" + (end - start) + "毫秒");

            System.out.println("有解：" + handle);
            System.out.println("=====================");

            int[][] arr = shudu.getArr();
            Arrays.stream(arr).forEach(a -> {
                Arrays.stream(a).forEach(b -> {
                    System.out.print(b + " \t");
                });
                System.out.println();
            });

        }
        public byte[] getRGB(int i, int j) {

            Row row = sheet.getRow(i); // 获取第一行
            Cell cell = row.getCell(j); // 获取第一个单元格

            XSSFCellStyle style = (XSSFCellStyle) cell.getCellStyle();

            // 获取填充模式
            short fillPattern = style.getFillPattern().getCode();
            if (fillPattern == FillPatternType.SOLID_FOREGROUND.getCode()) {
                // 获取填充颜色
                XSSFColor bgColor = style.getFillForegroundColorColor();
//            ((XSSFCellStyle) row.getCell(6).getCellStyle()).getFillForegroundColorColor().getRGB()
                if (bgColor != null) {
                    System.out.println(i + "," + j + "     Background color: RGB (" +
                        bgColor.getRGB()[0] + ", " +
                        bgColor.getRGB()[1] + ", " +
                        bgColor.getRGB()[2] + ")");

                    return bgColor.getRGB();
                }
            }
            return null;
        }
        public void collNeighbor(Coordinate pos, Set<Coordinate> set, byte[] rgb) {
            if (pos.getI() > 0) {
                Coordinate neighborPos = new Coordinate(pos.getI() - 1, pos.getJ());
                if (!addedSet.contains(neighborPos)) {
                    byte[] neighborRgb = getRGB(neighborPos.getI(), neighborPos.getJ());
                    if (rgb[0] == neighborRgb[0] && rgb[1] == neighborRgb[1] && rgb[2] == neighborRgb[2]) {
                        addedSet.add(neighborPos);
                        set.add(neighborPos);
                        collNeighbor(neighborPos, set, rgb);
                    }
                }
            }
            if (pos.getJ() > 0) {
                Coordinate neighborPos = new Coordinate(pos.getI(), pos.getJ()-1);
                if (!addedSet.contains(neighborPos)) {
                    byte[] neighborRgb = getRGB(neighborPos.getI(), neighborPos.getJ());
                    if (rgb[0] == neighborRgb[0] && rgb[1] == neighborRgb[1] && rgb[2] == neighborRgb[2]) {
                        addedSet.add(neighborPos);
                        set.add(neighborPos);
                        collNeighbor(neighborPos, set, rgb);
                    }
                }
            }
            if (pos.getI() < 8) {
                Coordinate neighborPos = new Coordinate(pos.getI()+1, pos.getJ());
                if (!addedSet.contains(neighborPos)) {
                    byte[] neighborRgb = getRGB(neighborPos.getI(), neighborPos.getJ());
                    if (rgb[0] == neighborRgb[0] && rgb[1] == neighborRgb[1] && rgb[2] == neighborRgb[2]) {
                        addedSet.add(neighborPos);
                        set.add(neighborPos);
                        collNeighbor(neighborPos, set, rgb);
                    }
                }
            }
            if (pos.getJ() < 8) {
                Coordinate neighborPos = new Coordinate(pos.getI(), pos.getJ()+1);
                if (!addedSet.contains(neighborPos)) {
                    byte[] neighborRgb = getRGB(neighborPos.getI(), neighborPos.getJ());
                    if (rgb[0] == neighborRgb[0] && rgb[1] == neighborRgb[1] && rgb[2] == neighborRgb[2]) {
                        addedSet.add(neighborPos);
                        set.add(neighborPos);
                        collNeighbor(neighborPos, set, rgb);
                    }
                }
            }
        }


        public boolean handle(int index) {
            // 9*9 数独 最大索引 80
            if (index > 80) {
                return true;
            }

            // 根据索引计算坐标
            int row = index / 9;
            int col = index % 9;

            // 如果当前位置为空，则获取当前位置合法值
            if (arr[row][col] == 0) {
                // 获取所有合法值的集合，因为笼的存在，很多位置不一定 1-9
                List<Integer> legalValues = getLegalValues(row, col);
                for (int value : legalValues) {
                    //赋值 记录
                    rowSet[row].add(value);
                    colSet[col].add(value);
                    boxSet[getBoxIndex(row, col)].add(value);
                    arr[row][col] = value;

                    // 如果之后的值都合法，则当前值正确，否则恢复为0，继续遍历
                    if (handle(index + 1)) {
                        return true;
                    } else {
                        rowSet[row].remove(value);
                        colSet[col].remove(value);
                        boxSet[getBoxIndex(row, col)].remove(value);
                        arr[row][col] = 0;
                    }
                }
                return false;
            } else {     // 如果当前位置有值，则处理下一个位置
                return handle(index + 1);
            }
        }

        /**
         * 获取该位置的所有合法值
         */
        private List<Integer> getLegalValues(int row, int col) {
            List<Integer> resultList = new ArrayList();
            int max = getMax(row, col);
            for (int i = 1; i <= max; i++) {
                if (isNoRepeat(row, col, i)) {
                    resultList.add(i);
                }
            }
            return resultList;
        }

        /**
         * 根据行、列 获取宫的索引
         */
        private int getBoxIndex(int row, int col) {
            return (row / 3) * 3 + (col / 3);
        }

        /**
         * 判断值是否与 行、列、宫 重复
         *
         * @return true 值合法，不重复
         * false 错误值，重复
         */
        private boolean isNoRepeat(int row, int col, int value) {
            if (row < 0 || row >= 9 || col < 0 || col >= 9 || value < 1 || value > 9) {
                return false;
            }
            //行
            if (rowSet[row].contains(value)) {
                return false;
            }
            // 列
            if (colSet[col].contains(value)) {
                return false;
            }
            // 宫
            if (boxSet[getBoxIndex(row, col)].contains(value)) {
                return false;
            }

            // 合法值 需要符合笼的和
            // 若能确定属于哪个笼，还可以筛选另一个加数大于10的值。
            // 如一个笼2个元素，和为15 ，则 不可能为 1、2、3、4、5 否则另一个数将大于9
            if ((getLongTarget(row, col) - getLongCurrentSum(row, col)) < value) {
                return false;
            }
            return true;
        }

        /**
         * 获取当前位置的最大值，根据 笼 或者 9
         * 若笼的数字不重复，则可以确定笼中数字的个数 或者 笼的位置。
         * 则 可以从小笼到大笼开始确定，遍历次数更少
         */
        private int getMax(int row, int col) {
            int longTarget = getLongTarget(row, col);
            int longCurrentSum = getLongCurrentSum(row, col);
            int current = longTarget - longCurrentSum;
            return (9 >= current) ? current : 9;
        }


        /**
         *  已提供方法 可直接使用
         * 获取笼所需数字和
         * @param i
         * @param j
         * @return
         */
        private int getLongTarget(int i, int j){
            return Integer.valueOf(rowList.get(i).get(j).toString());
        }

        /**
         *  已提供方法 可直接使用
         * 获取笼已填入数字和
         * @param i
         * @param j
         * @return
         */
        private int getLongCurrentSum(int i, int j){
            Coordinate pos = new Coordinate(i, j);
            for (Set<Coordinate> set : coll) {
                if (set.contains(pos)){
                    return set.stream().mapToInt(p -> arr[p.getI()][p.getJ()]).sum();
                }
            }
            throw new RuntimeException();
        }

        public LongShuDu(int[][] arr) {
            this.arr = arr;
        }

        @Data
        @Accessors(chain = true)
        @NoArgsConstructor
        @AllArgsConstructor
        static class Coordinate {
            int i;
            int j;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Coordinate coordinate)) return false;
                return i == coordinate.i && j == coordinate.j;
            }

            @Override
            public int hashCode() {
                return Objects.hash(i, j);
            }
        }
    }

    static class SimpleShuDu {
        private final boolean[][] line = new boolean[9][9];
        private final boolean[][] column = new boolean[9][9];
        private final boolean[][][] block = new boolean[3][3][9];
        private boolean valid = false;
        private final List<int[]> spaces = new ArrayList<>();

        public void solveSudoku(char[][] board) {
            for (int i = 0; i < 9; ++i) {
                for (int j = 0; j < 9; ++j) {
                    if (board[i][j] == '.') {
                        spaces.add(new int[]{i, j});
                    } else {
                        int digit = board[i][j] - '0' - 1;
                        line[i][digit] = column[j][digit] = block[i / 3][j / 3][digit] = true;
                    }
                }
            }

            dfs(board, 0);
        }

        public void dfs(char[][] board, int pos) {
            if (pos == spaces.size()) {
                valid = true;
                return;
            }

            int[] space = spaces.get(pos);
            int i = space[0], j = space[1];
            for (int digit = 0; digit < 9 && !valid; ++digit) {
                if (!line[i][digit] && !column[j][digit] && !block[i / 3][j / 3][digit]) {
                    line[i][digit] = column[j][digit] = block[i / 3][j / 3][digit] = true;
                    board[i][j] = (char) (digit + '0' + 1);
                    dfs(board, pos + 1);
                    line[i][digit] = column[j][digit] = block[i / 3][j / 3][digit] = false;
                }
            }
        }
    }

}

