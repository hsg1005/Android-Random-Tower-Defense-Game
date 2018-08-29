package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by 성규 on 2017-05-25.
 */

public class RunGame extends SurfaceView implements SurfaceHolder.Callback {

    private CMopMgr cMopMgr = new CMopMgr();
    private CBonusMopMgr cBonusMopMgr = new CBonusMopMgr();
    private CTowerMgr cTowerMgr = new CTowerMgr();
    private GameThread GameThread;

    //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear1);
    //Button newWave = (Button) findViewById(R.id.playGame);



    //////////////////////////////////////////////
    //////////////////////////////////////////////
    //////////////////////////////////////////////

    class CMopMgr {

        public final int nMopCnt = 20; // 몹 개수
        public CMop mop[] = new CMop[nMopCnt]; // 몹 만듬
        public int nUsedMopCnt = 0; // 생성되었던  몹 Count
       // public int nDieMopCnt = 0; // 죽은 몹 count
        public long lRegen = 500; // 다음몹 생성되는 시간(1초)
        public long lBeforRegen = System.currentTimeMillis();

        CMopMgr() {
            for (int i = 0; i < nMopCnt; i++) {
                mop[i] = new CMop();
            }
        }

        // 신규몹 추가 - bStart 플래그만 true 로 설정
        public void AddMop() {
            // 리젠 시간에 도달하면 몹 추가를 시도함
            if ((System.currentTimeMillis() - lBeforRegen) > lRegen) {
                lBeforRegen = System.currentTimeMillis();
            } else return;
            if (nUsedMopCnt >= (nMopCnt - 1)) return;
            mop[nUsedMopCnt].bUsed = true;

            nUsedMopCnt++;
        }

        // 이동 가능한 모든 몹들을 이동시킨다
        public void MoveMop() {
            for (int n = 0; n < nMopCnt; n++) {
                if (mop[n].bUsed == true) {
                    mop[n].MovePosition();
                }
            }
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////////
    //////////////////////////////////////////////////

    public class CBonusMopMgr {
        public CBonusMop bmop = new CBonusMop();
        public boolean bBonusMopSwitch = false;
        public long lBonusMopRegen = 60000; // 보너스몹 생성되는 시간(1분)
        public long lBonusMopBeforeRegen = System.currentTimeMillis();

        public CBonusMopMgr(){}

        public void Bonus_AddMop() {
            // 리젠 시간에 도달하고 true이면 몹 생성
            if(bBonusMopSwitch == false) return;
                bBonusMopSwitch = false;
            if ((System.currentTimeMillis() - lBonusMopBeforeRegen) > lBonusMopRegen) {
                bmop = new CBonusMop();
                bmop.bUsed = true;
                lBonusMopBeforeRegen = System.currentTimeMillis();
            }
            else return;
        }

        // 이동 가능한 모든 몹들을 이동시킨다
        public void Bonus_MoveMop() {
            if (bmop.bUsed == true)
                bmop.MovePosition();
            else return;
        }
    }

    ////////////////////////////////////////////////
    //////////////////////////////////////////////////
    ///////////////////////////////////////////////////

    public class CTower {
        public int nX = -1;        // 타워 X좌표
        public int nY = -1;        // 타워 Y좌표
        public int nMopIndex;                       // 공격 몹의 Index
        public long lAttackTime = 0;               // 공격시간
        public long lAttackSleep = 500;            // 공격 간격 0.5초
        public boolean bUsed = false;              // 사용되는 타워여부
        public int ntowerGrade = 1;                 //타워 등급

        public boolean bUsedMissile = false;     // 사용되는 미사일여부
        public int nMissilePos = 0;                // 미사일 진행 거리(0~5)
        public long lMissileSleep = 100;          // 미사일 1틱 간격(0.2초)
        public long lMissileTime = 0;             // 이전 미사일 진행했던 시간
        public int nMissileDistance = 250;          // 미사일 사정거리
        public int nMissileDamage = 50;           // 미사일 데미지
        public int nMissileX = 0;                 // 미사일 X좌표
        public int nMissileY = 0;                 // 미사일 Y좌표


        // 공격 가능한 몹 번호를 반환한다
        public int FindMop(int nTowerIndex) {
            for (int n = 0; n < cMopMgr.nMopCnt; n++) {

                // 죽었거나 생성되지 않은 몹이면 continue..
                if (cMopMgr.mop[n].bUsed == false) {
                    continue;
                }

                //몬스터와 타워 사이 거리
                int nX = cTowerMgr.tower[nTowerIndex].nX - cMopMgr.mop[n].nX;
                int nY = cTowerMgr.tower[nTowerIndex].nY - cMopMgr.mop[n].nY;


                nX = Math.abs(nX);
                nY = Math.abs(nY);

                if(ScreenConfig.getX(nX) <= ScreenConfig.getX(nMissileDistance) &&
                        ScreenConfig.getY(nY) <= ScreenConfig.getY(nMissileDistance))
                    return n;
            }

            if(cBonusMopMgr.bmop.bUsed == true){

                int nX = cTowerMgr.tower[nTowerIndex].nX - cBonusMopMgr.bmop.nX;
                int nY = cTowerMgr.tower[nTowerIndex].nY - cBonusMopMgr.bmop.nY;


                nX = Math.abs(nX);
                nY = Math.abs(nY);

                if(ScreenConfig.getX(nX) <= ScreenConfig.getX(nMissileDistance) &&
                        ScreenConfig.getY(nY) <= ScreenConfig.getY(nMissileDistance))
                    return 100;
            }
            return -1;
        }

        void AttackMop(int nMopIndex) {

            // 사용중인 타워가 아니라면 return
            if (bUsed == false) return;

            // 공격 간격이 안되었다면 return;
            if ((System.currentTimeMillis() - lAttackTime) > lAttackSleep) {
                lAttackTime = System.currentTimeMillis();
            } else return;

            // 미사일이 발사된 상태면 return
            if (bUsedMissile == true) return;

            // 타겟몹 설정
            this.nMopIndex = nMopIndex;
            // 미사일 발사
            bUsedMissile = true;


        }

        void MoveMissile() {
                    // 사용중인 타워가 아니면 return
                    if (bUsed == false) return;

                    // 미사일이 발사되지 않았으면 return
                    if (bUsedMissile == false) return;

                    // 미사일 진행 간격이 아니면 return
                    if ((System.currentTimeMillis() - lMissileTime) > lMissileSleep) {
                        lMissileTime = System.currentTimeMillis();
                    } else return;

                    // 미사일에 맞았다
                    if (nMissilePos > 9) {

                        // 미사일은 사용되지 않은 상태다
                        nMissilePos = 0;
                        bUsedMissile = false;

                        if(nMopIndex >= 0 && nMopIndex < cMopMgr.nMopCnt){
                            // 이미 죽어있다
                            if (cMopMgr.mop[nMopIndex].nMopHP < 0) {
                                return;
                            }
                            //데미지를 준다
                            cMopMgr.mop[nMopIndex].nMopHP -= nMissileDamage * ntowerGrade;

                            if (cMopMgr.mop[nMopIndex].nMopHP <= 0) {
                                // 몹은 죽었다
                                cMopMgr.mop[nMopIndex].bUsed = false;
                                //cMopMgr.nDieMopCnt += 1;
                            }
                            return;
                        }
                        else if(nMopIndex == 100){
                            if(cBonusMopMgr.bmop.nMopHP >0){
                                //데미지를 준다
                                cBonusMopMgr.bmop.nMopHP -= nMissileDamage * ntowerGrade;

                                if (cBonusMopMgr.bmop.nMopHP <= 0) {
                                    // 몹은 죽었다
                                    cBonusMopMgr.bmop.bUsed = false;

                                    //보너스몹을 잡으면 타워설치가능 횟수 1 증가
                                    cTowerMgr.nAbleTower += 1;
                                    //Toast.makeText(findViewById(R.id.RunGame).getContext(),"타워설치가능 횟수 증가",Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                }
            }
            // MopIndex가 0~20사이이면 일반몹으로 판단
            if(nMopIndex >= 0 && nMopIndex < cMopMgr.nMopCnt){
                int nMopX = cMopMgr.mop[nMopIndex].nX;
                int nMopY = cMopMgr.mop[nMopIndex].nY;

                // 몹과 타워의 거리
                int nWidth = nX - nMopX;
                int nHeight = nY - nMopY;

                // 현재 타워에서 몹까지의 비율을 나눠서 구해 더한다
                nMissileX = nX - (nWidth / (10 - nMissilePos));
                nMissileY = nY - (nHeight / (10 - nMissilePos));

                nMissilePos++;
                return;
            }
            //MopIndex가 100이면 보너스몹으로 판단
            else if(nMopIndex == 100){
                int nMopX = cBonusMopMgr.bmop.nX;
                int nMopY = cBonusMopMgr.bmop.nY;

                // 몹과 타워의 거리
                int nWidth = nX - nMopX;
                int nHeight = nY - nMopY;

                // 현재 타워에서 몹까지의 비율을 나눠서 구해 더한다
                nMissileX = nX - (nWidth / (10 - nMissilePos));
                nMissileY = nY - (nHeight / (10 - nMissilePos));

                nMissilePos++;
                return;
            }
        }
    }

    public class CLaserTower extends CTower{
        public long lMissileSleep = 200;          // 미사일 1틱 간격(0.2초)
        public int nMissileDamage = 50;           // 미사일 데미지
        public int nMissileDistance = 200;

        @Override
        void MoveMissile() {
            // 사용중인 타워가 아니면 return
            if (bUsed == false) return;

            // 미사일이 발사되지 않았으면 return
            if (bUsedMissile == false) return;

            // 미사일 진행 간격이 아니면 return
            if ((System.currentTimeMillis() - lMissileTime) > lMissileSleep) {
                lMissileTime = System.currentTimeMillis();
            } else return;

            // 미사일에 맞았다
            if (nMissilePos > 2) {

                // 미사일은 사용되지 않은 상태다
                nMissilePos = 0;
                bUsedMissile = false;

                if(nMopIndex >= 0 && nMopIndex < cMopMgr.nMopCnt){
                    // 이미 죽어있다
                    if (cMopMgr.mop[nMopIndex].nMopHP < 0) {
                        return;
                    }
                    //데미지를 준다
                    cMopMgr.mop[nMopIndex].nMopHP -= nMissileDamage * ntowerGrade;

                    if (cMopMgr.mop[nMopIndex].nMopHP <= 0) {
                        // 몹은 죽었다
                        cMopMgr.mop[nMopIndex].bUsed = false;
                        //cMopMgr.nDieMopCnt += 1;
                    }
                    return;
                }
                else if(nMopIndex == 100){
                    if(cBonusMopMgr.bmop.nMopHP >0){
                        //데미지를 준다
                        cBonusMopMgr.bmop.nMopHP -= nMissileDamage * ntowerGrade;

                        if (cBonusMopMgr.bmop.nMopHP <= 0) {
                            // 몹은 죽었다
                            cBonusMopMgr.bmop.bUsed = false;

                            //보너스몹을 잡으면 타워설치가능 횟수 1 증가
                            cTowerMgr.nAbleTower += 1;
                            return;
                        }
                    }
                }
            }
            // MopIndex가 0~20사이이면 일반몹으로 판단
            if(nMopIndex >= 0 && nMopIndex < cMopMgr.nMopCnt){

                nMissileX = cMopMgr.mop[nMopIndex].nX;
                nMissileY = cMopMgr.mop[nMopIndex].nY;

                nMissilePos++;
                return;
            }

            //MopIndex가 100이면 보너스몹으로 판단
            else if(nMopIndex == 100){

                nMissileX = cBonusMopMgr.bmop.nX;
                nMissileY = cBonusMopMgr.bmop.nY;

                nMissilePos++;
                return;
            }
        }
    }

    //////////////////////////////////////////////
    /////////////////////////////////////////////
    /////////////////////////////////////////////////

    public class CBoomTower extends CTower{

        public long lAttackSleep = 2000;            // 공격 간격 2초
        public long lMissileSleep = 100;          // 미사일 1틱 간격(0.2초)
        public int nMissileDistance = 300;        // 미사일 사정거리
        public int nMissileDamage = 80;           // 미사일 데미지

        @Override
        void MoveMissile() {
            // 사용중인 타워가 아니면 return
            if (bUsed == false) return;

            // 미사일이 발사되지 않았으면 return
            if (bUsedMissile == false) return;

            // 미사일 진행 간격이 아니면 return
            if ((System.currentTimeMillis() - lMissileTime) > lMissileSleep) {
                lMissileTime = System.currentTimeMillis();
            } else return;

            //첫번째와 마지막 몬스터는 공격 안한다
            if(nMopIndex == 0)
                nMopIndex++;
            if(nMopIndex == cMopMgr.nMopCnt)
                nMopIndex--;

            // 미사일에 맞았다
            if (nMissilePos > 9) {

                // 미사일은 사용되지 않은 상태다
                nMissilePos = 0;
                bUsedMissile = false;



                //일반 몬스터를 공격
                if(nMopIndex >= 0 && nMopIndex < cMopMgr.nMopCnt){
                    // 이미 죽어있다
                    if (cMopMgr.mop[nMopIndex].nMopHP < 0) {
                        return;
                    }
                    //스플레시 데미지를 준다
                    cMopMgr.mop[nMopIndex-1].nMopHP -= nMissileDamage/2 * ntowerGrade;
                    cMopMgr.mop[nMopIndex].nMopHP -= nMissileDamage * ntowerGrade;
                    cMopMgr.mop[nMopIndex+1].nMopHP -= nMissileDamage/2 * ntowerGrade;


                    if (cMopMgr.mop[nMopIndex - 1].nMopHP <= 0) {
                        // 몹은 죽었다
                        cMopMgr.mop[nMopIndex - 1].bUsed = false;
                    }
                    if (cMopMgr.mop[nMopIndex].nMopHP <= 0) {
                        // 몹은 죽었다
                        cMopMgr.mop[nMopIndex].bUsed = false;
                    }
                    if (cMopMgr.mop[nMopIndex + 1].nMopHP <= 0) {
                        // 몹은 죽었다
                        cMopMgr.mop[nMopIndex + 1].bUsed = false;
                    }
                    return;
                }

                // 보너스 몬스터를 공격
                else if(nMopIndex == 100){
                    if(cBonusMopMgr.bmop.nMopHP >0){
                        //보스몹은 더 강한 데미지를 준다
                        cBonusMopMgr.bmop.nMopHP -= nMissileDamage * 2 * ntowerGrade;

                        if (cBonusMopMgr.bmop.nMopHP <= 0) {
                            // 몹은 죽었다
                            cBonusMopMgr.bmop.bUsed = false;

                            //보너스몹을 잡으면 타워설치가능 횟수 1 증가
                            cTowerMgr.nAbleTower += 1;
                            return;
                        }
                    }
                }
            }
            // MopIndex가 0~20사이이면 일반몹으로 판단
            if(nMopIndex >= 0 && nMopIndex < cMopMgr.nMopCnt){
                int nMopX = cMopMgr.mop[nMopIndex].nX;
                int nMopY = cMopMgr.mop[nMopIndex].nY;

                // 몹과 타워의 거리
                int nWidth = nX - nMopX;
                int nHeight = nY - nMopY;

                // 현재 타워에서 몹까지의 비율을 나눠서 구해 더한다
                nMissileX = nX - (nWidth / (10 - nMissilePos));
                nMissileY = nY - (nHeight / (10 - nMissilePos));

                nMissilePos++;
                return;
            }
            //MopIndex가 100이면 보너스몹으로 판단
            else if(nMopIndex == 100){
                int nMopX = cBonusMopMgr.bmop.nX;
                int nMopY = cBonusMopMgr.bmop.nY;

                // 몹과 타워의 거리
                int nWidth = nX - nMopX;
                int nHeight = nY - nMopY;

                // 현재 타워에서 몹까지의 비율을 나눠서 구해 더한다
                nMissileX = nX - (nWidth / (10 - nMissilePos));
                nMissileY = nY - (nHeight / (10 - nMissilePos));

                nMissilePos++;
                return;
            }
        }
    }
    ///////////////////////////////////////////
    ///////////////////////////////////////////////
    ////////////////////////////////////////////


    public class CTowerMgr {
        public static final int nMaxTowerCnt = 40;        // 최대 설치 가능 타워
        public CTower tower[] = new CTower[nMaxTowerCnt];  // 타워 객체
        public int nAbleTower = 3;                          // 설치 가능 타워
        public int nUsedTowerCnt = 0;                      // 사용되는 타워 Count
        public int nUpgradeTowerCnt = 0;

        CTowerMgr() {
            for (int n = 0; n < nMaxTowerCnt; n++) {
                tower[n] = new CTower();
            }
        }

        public int UpgradeTower(int towerIndex){
            int n;

            //최대 등급이 되면 리턴
            if(tower[towerIndex].ntowerGrade >= 8)
                return -1;

            for(n = 0; n < nUsedTowerCnt; n++){

                //동일한 타워에 대한 업그레이드 방지
                if(towerIndex == n)
                    continue;
                //등급이 같지 않으면 컨티뉴
                if(!(tower[towerIndex].ntowerGrade == tower[n].ntowerGrade))
                    continue;

                if(tower[towerIndex].bUsed == true && tower[n].bUsed == true){

                    //합성당하는 타워 사용x
                    tower[n].bUsed = false;
                    tower[n].ntowerGrade = 1;
                    tower[n].nX = -1;
                    tower[n].nY = -1;

                    //현재 타워 등급 저장
                    int temp_Grade = tower[towerIndex].ntowerGrade;
                    int temp_Damage= tower[towerIndex].nMissileDamage;

                    //업그레이드 타워 건설 //건설 0~2까지의 정수
                    Random random = new Random();
                    int nRandom = random.nextInt(3);

                    switch (nRandom){
                        case 0:
                            tower[towerIndex] = new CTower();
                            tower[towerIndex].nMissileDamage = temp_Damage + 100;
                            break;
                        case 1:
                            tower[towerIndex] = new CLaserTower();
                            tower[towerIndex].nMissileDamage = temp_Damage + 10;
                            break;
                        case 2:
                            tower[towerIndex] = new CBoomTower();
                            tower[towerIndex].nMissileDamage = temp_Damage + 60;
                            break;
                        default: break;
                    }

                    //업그레이드된 타워 옵션 변경 및 사용
                    tower[towerIndex].ntowerGrade = temp_Grade + 1;
                    tower[towerIndex].bUsed = true;


                    //업그레이드 횟수 증가
                    nUpgradeTowerCnt += 1;

                    for(int i = n; i < nUsedTowerCnt - 1; i++){

                        //합성당하는 타워 자리에 배열 공백 제거
                        tower[i] = tower[i + 1];
                        //타워인덱스가 움직이면 값을 리턴
                        if((i + 1) == towerIndex)
                            towerIndex = i;
                        //배열 끝처리
                        if((i + 1) == nMaxTowerCnt){
                            tower[i+1].bUsed = false;
                            tower[i+1].ntowerGrade = 1;
                            tower[i+1].nX = -1;
                            tower[i+1].nY = -1;
                            break;
                        }
                    }

                    nUsedTowerCnt--;

                    tower[nUsedTowerCnt] = new CTower();
                    tower[nUsedTowerCnt].bUsed = false;
                    tower[nUsedTowerCnt].ntowerGrade = 1;
                    tower[nUsedTowerCnt].nX = -1;
                    tower[nUsedTowerCnt].nY = -1;

                    return towerIndex;
                }
            }
            return -1;
        }

        // 타워추가는 Used만 변경
        public void AddTower(int x, int y) {

            // 중복 위치의 타워 검사
            for(int n = 0; n < nUsedTowerCnt; n++){
                if(tower[n].nX == x && tower[n].nY == y){

                    //타워 업그레이드
                    int tmp_index = UpgradeTower(n);

                    if(tmp_index >= 0){
                        tower[tmp_index].nX = x;
                        tower[tmp_index].nY = y;
                        return;
                    }
                return;
                }
            }

            // 타워 최대 갯수 초과 또는 설치 가능한 타워 부족
            if (0 >= nAbleTower || nUsedTowerCnt >= nMaxTowerCnt){
                Toast.makeText(findViewById(R.id.RunGame).getContext(),"더 이상 타워를 설치할 수 없습니다.",Toast.LENGTH_LONG).show();
                return;

            }



            // 몬스터 경로 타워설치 금지
            if(x == ScreenConfig.getX(200)){
                if(y <= ScreenConfig.getY(500) || y >= ScreenConfig.getY(900)) return;
            }
            if(x == ScreenConfig.getX(500)){
                if(y >= ScreenConfig.getY(200) && y <= ScreenConfig.getY(1200)) return;
            }
            if(x == ScreenConfig.getX(800)){
                if(y >= ScreenConfig.getY(200) && y <= ScreenConfig.getY(500)) return;
                else if(y >= ScreenConfig.getY(900) && y <= ScreenConfig.getY(1200)) return;
            }
            if(y == ScreenConfig.getY(200)){
                if(x >= ScreenConfig.getX(500) && x <= ScreenConfig.getX(800)) return;
            }
            if(y == ScreenConfig.getY(500)){
                if(x >= ScreenConfig.getX(200) && x <= ScreenConfig.getX(800)) return;
            }
            if(y == ScreenConfig.getY(900)){
                if(x >= ScreenConfig.getX(200) && x <= ScreenConfig.getX(800)) return;
            }
            if(y == ScreenConfig.getY(1200)){
                if(x >= ScreenConfig.getX(500) && x <= ScreenConfig.getX(800)) return;
            }


                tower[nUsedTowerCnt].nX = x;
                tower[nUsedTowerCnt].nY = y;
                tower[nUsedTowerCnt].bUsed = true;
                nUsedTowerCnt++;
                nAbleTower--;
        }
    }

    ///////////////////////////////////////////////
    /////////////////////////////////////////////////////
    //////////////////////////////////////////////////////

    class GameThread extends Thread {
        private Bitmap bmpBackground;          // 배경
        private Bitmap bmpMop;                 // 몹
        private Bitmap bmpMissile;            // 미사일
        private Bitmap bmpMenu;               // 게임 선택창
        private Bitmap bmpBonusMop;           //보너스 몹
        private Bitmap bmpTower;              // 타워
        private Bitmap bmpLaserTower;         //레이저 타워
        private Bitmap bmpBoomTower;            //폭탄 타워
        private Bitmap bmpBoomMissile;          //폭탄 미사일
        private Bitmap bmpExplosionBoom;        //폭탄 폭발
        private SurfaceHolder SurfaceHolder;

        public boolean run_Thread;             // 쓰레드 시작
        private long lBeforTimeToThread = System.currentTimeMillis();
        private long nSleepToThread = 35000; // 다음 스테이지까지 남은 시간 35초
        double dRandomHP; // 랜덤 HP 값 저장

        int dDebug_coordinateX = 0;
        int dDebug_coordinateY = 0;
        int dDebug_coordinateVX = 0;
        int dDebug_coordinateVY = 0;




        //생성자
        public GameThread(SurfaceHolder surfaceholder, Context context) {

            SurfaceHolder = surfaceholder;
            Resources res = context.getResources();

            // 그림 이미지
            Bitmap temp_bmpBg = BitmapFactory.decodeResource(res, R.drawable.map);
            bmpBackground = Bitmap.createScaledBitmap(temp_bmpBg, ScreenConfig.getScreenWidth(), ScreenConfig.getScreenHeight(), true); // 배경 이미지를 화면에 맞게 설정

            Bitmap temp_bmpMenu = BitmapFactory.decodeResource(res, R.drawable.menu);
            bmpMenu = Bitmap.createScaledBitmap(temp_bmpMenu, ScreenConfig.getX(550), ScreenConfig.getY(600), true);

            Bitmap temp_bmpBonusMop =  BitmapFactory.decodeResource(res, R.drawable.bonusmop);
            bmpBonusMop = Bitmap.createScaledBitmap(temp_bmpBonusMop, ScreenConfig.getX(100), ScreenConfig.getY(100), true);

            Bitmap temp_bmpLaserTower =  BitmapFactory.decodeResource(res, R.drawable.lasertower);
            bmpLaserTower = Bitmap.createScaledBitmap(temp_bmpLaserTower, ScreenConfig.getX(100), ScreenConfig.getY(100), true);

            Bitmap temp_bmpBoomTower =  BitmapFactory.decodeResource(res, R.drawable.boomtower);
            bmpBoomTower = Bitmap.createScaledBitmap(temp_bmpBoomTower, ScreenConfig.getX(100), ScreenConfig.getY(100), true);

            Bitmap temp_bmpBoomMissile =  BitmapFactory.decodeResource(res, R.drawable.boommissile);
            bmpBoomMissile = Bitmap.createScaledBitmap(temp_bmpBoomMissile, ScreenConfig.getX(50), ScreenConfig.getY(50), true);

            Bitmap temp_bmpExplosionBoom =  BitmapFactory.decodeResource(res, R.drawable.explosionboom);
            bmpExplosionBoom = Bitmap.createScaledBitmap(temp_bmpExplosionBoom, ScreenConfig.getX(100), ScreenConfig.getY(100), true);

            bmpMop = BitmapFactory.decodeResource(res, R.drawable.mop);
            bmpMissile = BitmapFactory.decodeResource(res, R.drawable.missile);
            bmpTower = BitmapFactory.decodeResource(res, R.drawable.tower);


            // 쓰레드가 작동하게 설정
            run_Thread = true;
        }

        //타워 추가
        void AddTower(int x, int y) {
            cTowerMgr.AddTower(x, y);
            dDebug_coordinateX = x;
            dDebug_coordinateY = y;
            dDebug_coordinateVX = ScreenConfig.getVX(x);
            dDebug_coordinateVY = ScreenConfig.getVY(y);

        }

        //스테이지에서 몹을 재생성
        public void StageMgr() {
            if ((System.currentTimeMillis() - lBeforTimeToThread) > nSleepToThread) {
                lBeforTimeToThread = System.currentTimeMillis();

                double dRandomSpeed = (Math.random() * 3) + 1; // 1.0 ~ 4.0 사이의 난수
                dRandomHP += (Math.random() + 1)  * 30;

                //스테이지 카운터 증가
                GameState.nGameStageCnt += 1 ;
                //설치 가능한 타워 증가
                cTowerMgr.nAbleTower += 2;
                //사용중인 몹 초기화
                cMopMgr.nUsedMopCnt = 0;

                //다시 몹을 생성
                for (int i = 0; i < cMopMgr.nMopCnt; i++) {
                    cMopMgr.mop[i] = new CMop();
                    cMopMgr.mop[i].nSpeed *= dRandomSpeed;
                    cMopMgr.mop[i].nMopHP += dRandomHP;
               }

               cBonusMopMgr.bmop.nSpeed *= dRandomSpeed/2;
                cBonusMopMgr.bmop.nMopHP += dRandomHP;
            }
        }


        public void run() {

            while (run_Thread) {
                Canvas canvas = null;
                try {
                    Paint paint = new Paint();
                    canvas = SurfaceHolder.lockCanvas(null);

                    synchronized (SurfaceHolder) {
                        Rect rcSrc = new Rect();
                        Rect rcDest = new Rect();
                        rcSrc.set(0, 0, ScreenConfig.getScreenWidth(), ScreenConfig.getScreenHeight());
                        rcDest.set(0, 0, ScreenConfig.getScreenWidth(), ScreenConfig.getScreenHeight());

                        Rect rcMenu = new Rect();
                        rcMenu.set(ScreenConfig.getX(550), ScreenConfig.getY(1400), ScreenConfig.getScreenWidth(), ScreenConfig.getScreenHeight());

                        // 배경 (화면 가득 채우게 크기조절)
                        canvas.drawBitmap(bmpBackground, rcSrc, rcDest, null);
                        canvas.drawBitmap(bmpMenu, null, rcMenu, null);

                        StageMgr(); // 스테이지마다 몹을 재생성
                        cMopMgr.AddMop();
                        cMopMgr.MoveMop();
                        cBonusMopMgr.Bonus_AddMop();
                        cBonusMopMgr.Bonus_MoveMop();

                        // 다음 스테이지 스위치
                        if(GameState.bNewWave == true){
                            GameState.bNewWave = false;
                            lBeforTimeToThread = 0;
                        }

                        // 몹을 그린다
                        for (int n = 0; n < cMopMgr.nMopCnt; n++) {
                            if (cMopMgr.mop[n].bUsed == true) {

                                canvas.drawBitmap(bmpMop, cMopMgr.mop[n].nX, cMopMgr.mop[n].nY, null);
                            }
                        }

                        // 보너스 몹을 그린다
                        if(cBonusMopMgr.bmop.bUsed == true){
                            canvas.drawBitmap(bmpBonusMop, cBonusMopMgr.bmop.nX, cBonusMopMgr.bmop.nY, null);
                        }
                    }

                    for (int n = 0; n < cTowerMgr.nMaxTowerCnt; n++) {

                        //타워가 사용되는지 확인한다
                        if (cTowerMgr.tower[n].bUsed == true) {
                            //타워 배경을 그린다
                            switch (cTowerMgr.tower[n].ntowerGrade){
                                case 1: break;

                                case 2: paint.setColor(Color.rgb(150,50,255));
                                        canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                        break; //보
                                case 3: paint.setColor(Color.rgb(00,00,255));
                                        canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                        break; //파
                                case 4: paint.setColor(Color.rgb(00,255,255));
                                        canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                        break; //하늘
                                case 5: paint.setColor(Color.rgb(255,255,80));
                                    canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                    break; //노
                                case 6: paint.setColor(Color.rgb(255,180,00));
                                    canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                    break; //주
                                case 7: paint.setColor(Color.rgb(255,00,00));
                                    canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                    break; //빨
                                case 8: paint.setColor(Color.rgb(255,00,255));
                                    canvas.drawRect(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,cTowerMgr.tower[n].nX+ScreenConfig.getX(100), cTowerMgr.tower[n].nY+ScreenConfig.getY(100), paint);
                                    break; //핑크
                                default: break;
                            }

                            //타워를 그린다
                            //레이저 타워를 그린다
                            if (cTowerMgr.tower[n] instanceof CLaserTower) {
                                canvas.drawBitmap(bmpLaserTower, cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY, null);
                            }
                            //폭탄 타워를 그린다
                            else if (cTowerMgr.tower[n] instanceof CBoomTower) {
                                canvas.drawBitmap(bmpBoomTower, cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY, null);
                            }
                            //노말 타워
                            else{
                                canvas.drawBitmap(bmpTower, cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY, null);
                            }


                            // 미사일을 그린다
                            if (cTowerMgr.tower[n].bUsedMissile == true) {

                                //레이저타워가 미사일을 쏜다
                                if(cTowerMgr.tower[n] instanceof CLaserTower){
                                    paint.setColor(Color.RED);
                                    paint.setStrokeWidth(10f);
                                    canvas.drawLine(cTowerMgr.tower[n].nX, cTowerMgr.tower[n].nY,
                                            cTowerMgr.tower[n].nMissileX, cTowerMgr.tower[n].nMissileY, paint);
                                }
                                //폭탄타워가 미사일을 쏜다
                                else if(cTowerMgr.tower[n] instanceof CBoomTower){
                                    canvas.drawBitmap(bmpBoomMissile, cTowerMgr.tower[n].nMissileX, cTowerMgr.tower[n].nMissileY, paint);
                                    if(cTowerMgr.tower[n].nMissilePos >= 10)
                                        canvas.drawBitmap(bmpExplosionBoom, cTowerMgr.tower[n].nMissileX, cTowerMgr.tower[n].nMissileY, paint);
                                }
                                //노멀 타워가 미사일을 쏜다
                                else{
                                    canvas.drawBitmap(bmpMissile, cTowerMgr.tower[n].nMissileX, cTowerMgr.tower[n].nMissileY, paint);
                                }

                            } else {

                                // 근처의 몹을 검색
                                int nMop = cTowerMgr.tower[n].FindMop(n);

                                // 찾은 몹을 공격
                                if (nMop >= 0) {
                                    cTowerMgr.tower[n].AttackMop(nMop);
                                }

                            }
                            cTowerMgr.tower[n].MoveMissile();
                        }
                    }

                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.BLUE);  //화면 캔버스를 하얀색으로 표현한다.
                    paint.setTextSize(ScreenConfig.getX(50));

                    canvas.drawText("타워 설치 : " + (cTowerMgr.nUsedTowerCnt + " / " + CTowerMgr.nMaxTowerCnt), ScreenConfig.getX(50), ScreenConfig.getY(50), paint);
                    canvas.drawText("                                                남은 생명 : " + (10 - GameState.nOverCount) , ScreenConfig.getX(50), ScreenConfig.getY(50), paint);
                    canvas.drawText("설치 가능 : " + (cTowerMgr.nAbleTower), ScreenConfig.getX(50), ScreenConfig.getY(100), paint);
                    canvas.drawText("디버깅용 좌표: " + dDebug_coordinateX + " , " + dDebug_coordinateY + " , " + dDebug_coordinateVX + " , " + dDebug_coordinateVY,
                            ScreenConfig.getX(50), ScreenConfig.getY(150), paint);


                    paint.setColor(Color.BLACK);
                    paint.setTextSize(ScreenConfig.getX(30));
                    canvas.drawText("스테이지 : " + (GameState.nGameStageCnt), ScreenConfig.getX(50), ScreenConfig.getY(1500), paint);
                    canvas.drawText("다음 스테이지 : " + ((nSleepToThread - (System.currentTimeMillis() - lBeforTimeToThread))/1000), ScreenConfig.getX(50), ScreenConfig.getY(1600),paint);
                    canvas.drawText("몬스터 체력 : " + (50 + (int)dRandomHP), ScreenConfig.getX(50), ScreenConfig.getY(1700), paint);
                    canvas.drawText("몬스터 스피드 : " + cMopMgr.mop[1].nSpeed, ScreenConfig.getX(50), ScreenConfig.getY(1800), paint);

                    if(0 >= (cBonusMopMgr.lBonusMopBeforeRegen - (System.currentTimeMillis() - cBonusMopMgr.lBonusMopRegen))/1000){
                        canvas.drawText("보너스 몬스터 : 생성 가능!!", ScreenConfig.getX(50), ScreenConfig.getY(1900), paint);
                    }else{
                        canvas.drawText("보너스 몬스터 : " + ((cBonusMopMgr.lBonusMopBeforeRegen - (System.currentTimeMillis() - cBonusMopMgr.lBonusMopRegen))/1000), ScreenConfig.getX(50), ScreenConfig.getY(1900), paint);
                    }

                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        SurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public RunGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        GameThread = new GameThread(holder, context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub


        if (event.getAction() == MotionEvent.ACTION_UP) {

            //boolean MenuButton;
            int nEventX = (int) event.getX();
            int nEventY = (int) event.getY();

                // 맵 최대 크기이면 동작 x
                if(nEventX == ScreenConfig.getScreenWidth() ||
                        nEventY == ScreenConfig.getScreenHeight())
                    return true;

                // 버튼 동작
                if(nEventY > ScreenConfig.getY(1400)){

                    // 상태창을 클릭하면 리턴
                   if(nEventX < ScreenConfig.getX(550)) return true;

                    if (nEventX < ScreenConfig.getX(825)){
                        if(nEventY < ScreenConfig.getY(1700)){     // 게임시작

                        }
                        else {                                     // 보너스
                            cBonusMopMgr.bBonusMopSwitch = true;
                        }
                    }
                    else{ //nEventX >= ScreenConfig.getX(825)
                        if(nEventY < ScreenConfig.getY(1700)){     // 새 웨이브
                            GameState.bNewWave = true;
                        }
                        else {}                                     // 게임 종료료
                    }
                   return true;
                }


                GameThread.AddTower(
                        ScreenConfig.getX(ScreenConfig.getVX(event.getX()/100)*100),
                        ScreenConfig.getY(ScreenConfig.getVY(event.getY()/100)*100)
                );
        }
        return true;
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

        GameThread.start();

    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }
}