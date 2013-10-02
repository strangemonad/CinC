;:   Copyright (c) Nicola Mometto, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns cinc.compiler.jvm.bytecode.intrinsics
  (:import (org.objectweb.asm Opcodes)))

(def intrinsic
  {"public static double clojure.lang.Numbers.add(double,double)"                [Opcodes/DADD]
   "public static long clojure.lang.Numbers.and(long,long)"                      [Opcodes/LAND]
   "public static long clojure.lang.Numbers.or(long,long)"                       [Opcodes/LOR]
   "public static long clojure.lang.Numbers.xor(long,long)"                      [Opcodes/LXOR]
   "public static double clojure.lang.Numbers.multiply(double,double)"           [Opcodes/DMUL]
   "public static double clojure.lang.Numbers.divide(double,double)"             [Opcodes/DDIV]
   "public static long clojure.lang.Numbers.remainder(long,long)"                [Opcodes/LREM]
   "public static long clojure.lang.Numbers.shiftLeft(long,long)"                [Opcodes/L2I Opcodes/LSHL]
   "public static long clojure.lang.Numbers.shiftRight(long,long)"               [Opcodes/L2I Opcodes/LSHR]
   "public static double clojure.lang.Numbers.minus(double)"                     [Opcodes/DNEG]
   "public static double clojure.lang.Numbers.minus(double,double)"              [Opcodes/DSUB]
   "public static double clojure.lang.Numbers.inc(double)"                       [Opcodes/DCONST_1 Opcodes/DADD]
   "public static double clojure.lang.Numbers.dec(double)"                       [Opcodes/DCONST_1 Opcodes/DSUB]
   "public static long clojure.lang.Numbers.quotient(long,long)"                 [Opcodes/LDIV]
   "public static int clojure.lang.Numbers.shiftLeftInt(int,int)"                [Opcodes/ISHL]
   "public static int clojure.lang.Numbers.shiftRightInt(int,int)"               [Opcodes/ISHR]
   "public static int clojure.lang.Numbers.unchecked_int_add(int,int)"           [Opcodes/IADD]
   "public static int clojure.lang.Numbers.unchecked_int_subtract(int,int)"      [Opcodes/ISUB]
   "public static int clojure.lang.Numbers.unchecked_int_negate(int)"            [Opcodes/INEG]
   "public static int clojure.lang.Numbers.unchecked_int_inc(int)"               [Opcodes/ICONST_1 Opcodes/IADD]
   "public static int clojure.lang.Numbers.unchecked_int_dec(int)"               [Opcodes/ICONST_1 Opcodes/ISUB]
   "public static int clojure.lang.Numbers.unchecked_int_multiply(int,int)"      [Opcodes/IMUL]
   "public static int clojure.lang.Numbers.unchecked_int_divide(int,int)"        [Opcodes/IDIV]
   "public static int clojure.lang.Numbers.unchecked_int_remainder(int,int)"     [Opcodes/IREM]
   "public static long clojure.lang.Numbers.unchecked_add(long,long)"            [Opcodes/LADD]
   "public static double clojure.lang.Numbers.unchecked_add(double,double)"      [Opcodes/DADD]
   "public static long clojure.lang.Numbers.unchecked_minus(long)"               [Opcodes/LNEG]
   "public static double clojure.lang.Numbers.unchecked_minus(double)"           [Opcodes/DNEG]
   "public static double clojure.lang.Numbers.unchecked_minus(double,double)"    [Opcodes/DSUB]
   "public static long clojure.lang.Numbers.unchecked_minus(long,long)"          [Opcodes/LSUB]
   "public static long clojure.lang.Numbers.unchecked_multiply(long,long)"       [Opcodes/LMUL]
   "public static double clojure.lang.Numbers.unchecked_multiply(double,double)" [Opcodes/DMUL]
   "public static double clojure.lang.Numbers.unchecked_inc(double)"             [Opcodes/DCONST_1 Opcodes/DADD]
   "public static long clojure.lang.Numbers.unchecked_inc(long)"                 [Opcodes/LCONST_1 Opcodes/LADD]
   "public static double clojure.lang.Numbers.unchecked_dec(double)"             [Opcodes/DCONST_1 Opcodes/DSUB]
   "public static long clojure.lang.Numbers.unchecked_dec(long)"                 [Opcodes/LCONST_1 Opcodes/LSUB]

   "public static short clojure.lang.RT.aget(short[]int)"                        [Opcodes/SALOAD]
   "public static float clojure.lang.RT.aget(float[]int)"                        [Opcodes/FALOAD]
   "public static double clojure.lang.RT.aget(double[]int)"                      [Opcodes/DALOAD]
   "public static int clojure.lang.RT.aget(int[]int)"                            [Opcodes/IALOAD]
   "public static long clojure.lang.RT.aget(long[]int)"                          [Opcodes/LALOAD]
   "public static char clojure.lang.RT.aget(char[]int)"                          [Opcodes/CALOAD]
   "public static byte clojure.lang.RT.aget(byte[]int)"                          [Opcodes/BALOAD]
   "public static boolean clojure.lang.RT.aget(boolean[]int)"                    [Opcodes/BALOAD]
   "public static java.lang.Object clojure.lang.RT.aget(java.lang.Object[]int)"  [Opcodes/AALOAD]
   "public static int clojure.lang.RT.alength(int[])"                            [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(long[])"                           [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(char[])"                           [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(java.lang.Object[])"               [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(byte[])"                           [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(float[])"                          [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(short[])"                          [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(boolean[])"                        [Opcodes/ARRAYLENGTH]
   "public static int clojure.lang.RT.alength(double[])"                         [Opcodes/ARRAYLENGTH]

   "public static double clojure.lang.RT.doubleCast(long)"                       [Opcodes/L2D]
   "public static double clojure.lang.RT.doubleCast(double)"                     [Opcodes/NOP]
   "public static double clojure.lang.RT.doubleCast(float)"                      [Opcodes/F2D]
   "public static double clojure.lang.RT.doubleCast(int)"                        [Opcodes/I2D]
   "public static double clojure.lang.RT.doubleCast(short)"                      [Opcodes/I2D]
   "public static double clojure.lang.RT.doubleCast(byte)"                       [Opcodes/I2D]
   "public static double clojure.lang.RT.uncheckedDoubleCast(double)"            [Opcodes/NOP]
   "public static double clojure.lang.RT.uncheckedDoubleCast(float)"             [Opcodes/F2D]
   "public static double clojure.lang.RT.uncheckedDoubleCast(long)"              [Opcodes/L2D]
   "public static double clojure.lang.RT.uncheckedDoubleCast(int)"               [Opcodes/I2D]
   "public static double clojure.lang.RT.uncheckedDoubleCast(short)"             [Opcodes/I2D]
   "public static double clojure.lang.RT.uncheckedDoubleCast(byte)"              [Opcodes/I2D]
   "public static long clojure.lang.RT.longCast(long)"                           [Opcodes/NOP]
   "public static long clojure.lang.RT.longCast(short)"                          [Opcodes/I2L]
   "public static long clojure.lang.RT.longCast(byte)"                           [Opcodes/I2L]
   "public static long clojure.lang.RT.longCast(int)"                            [Opcodes/I2L]
   "public static int clojure.lang.RT.uncheckedIntCast(long)"                    [Opcodes/L2I]
   "public static int clojure.lang.RT.uncheckedIntCast(double)"                  [Opcodes/D2I]
   "public static int clojure.lang.RT.uncheckedIntCast(byte)"                    [Opcodes/NOP]
   "public static int clojure.lang.RT.uncheckedIntCast(short)"                   [Opcodes/NOP]
   "public static int clojure.lang.RT.uncheckedIntCast(char)"                    [Opcodes/NOP]
   "public static int clojure.lang.RT.uncheckedIntCast(int)"                     [Opcodes/NOP]
   "public static int clojure.lang.RT.uncheckedIntCast(float)"                   [Opcodes/F2I]
   "public static long clojure.lang.RT.uncheckedLongCast(short)"                 [Opcodes/I2L]
   "public static long clojure.lang.RT.uncheckedLongCast(float)"                 [Opcodes/F2L]
   "public static long clojure.lang.RT.uncheckedLongCast(double)"                [Opcodes/D2L]
   "public static long clojure.lang.RT.uncheckedLongCast(byte)"                  [Opcodes/I2L]
   "public static long clojure.lang.RT.uncheckedLongCast(long)"                  [Opcodes/NOP]
   "public static long clojure.lang.RT.uncheckedLongCast(int)"                   [Opcodes/I2L]})

(def intrinsic-predicate
  {"public static boolean clojure.lang.Numbers.lt(double,double)"                [Opcodes/DCMPG Opcodes/IFGE]
   "public static boolean clojure.lang.Numbers.lt(long,long)"                    [Opcodes/LCMP Opcodes/IFGE]
   "public static boolean clojure.lang.Numbers.equiv(double,double)"             [Opcodes/DCMPL Opcodes/IFNE]
   "public static boolean clojure.lang.Numbers.equiv(long,long)"                 [Opcodes/LCMP Opcodes/IFNE]
   "public static boolean clojure.lang.Numbers.lte(double,double)"               [Opcodes/DCMPG Opcodes/IFGT]
   "public static boolean clojure.lang.Numbers.lte(long,long)"                   [Opcodes/LCMP Opcodes/IFGT]
   "public static boolean clojure.lang.Numbers.gt(long,long)"                    [Opcodes/LCMP Opcodes/IFLE]
   "public static boolean clojure.lang.Numbers.gt(double,double)"                [Opcodes/DCMPL Opcodes/IFLE]
   "public static boolean clojure.lang.Numbers.gte(long,long)"                   [Opcodes/LCMP Opcodes/IFLT]
   "public static boolean clojure.lang.Numbers.gte(double,double)"               [Opcodes/DCMPL Opcodes/IFLT]
   "public static boolean clojure.lang.Util.equiv(long,long)"                    [Opcodes/LCMP Opcodes/IFNE]
   "public static boolean clojure.lang.Util.equiv(boolean,boolean)"              [Opcodes/IF_ICMPNE]
   "public static boolean clojure.lang.Util.equiv(double,double)"                [Opcodes/DCMPL Opcodes/IFNE]

   "public static boolean clojure.lang.Numbers.isZero(double)"                   [Opcodes/DCONST_0 Opcodes/DCMPL Opcodes/IFNE]
   "public static boolean clojure.lang.Numbers.isZero(long)"                     [Opcodes/LCONST_0 Opcodes/LCMP Opcodes/IFNE]
   "public static boolean clojure.lang.Numbers.isPos(long)"                      [Opcodes/LCONST_0 Opcodes/LCMP Opcodes/IFLE]
   "public static boolean clojure.lang.Numbers.isPos(double)"                    [Opcodes/DCONST_0 Opcodes/DCMPL Opcodes/IFLE]
   "public static boolean clojure.lang.Numbers.isNeg(long)"                      [Opcodes/LCONST_0 Opcodes/LCMP Opcodes/IFGE]
   "public static boolean clojure.lang.Numbers.isNeg(double)"                    [Opcodes/DCONST_0 Opcodes/DCMPG Opcodes/IFGE]})
