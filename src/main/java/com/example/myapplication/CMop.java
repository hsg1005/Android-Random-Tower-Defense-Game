package com.example.myapplication;

/**
 * Created by 성규 on 2017-06-01.
 */

public class CMop {
    public int nX = ScreenConfig.getX(200);
    public int nY = 0;
    public long lBeforTime = System.currentTimeMillis();       // 이전에 움직였던 밀리세컨드
    public long nSleep = 5;           // 이전과 다음이동사이의 밀리세컨드
    public int nSpeed = ScreenConfig.getX(5);           // 한 이동 단위에 움직일 pixel 숫자
    public int nMoveArea = 1;        // 이동할 구역
    public int nMopHP = 50;       // 몹체력
    public boolean bUsed = false;         // 생성되었나
    //public boolean bDie = false;          // 몹이 죽었나 - 재생되지 않음
    public int nDirection = -1;       // 몹의 방향
    // x, y, 방향(동쪽이 1이며 시계방향으로 증가함)

    public int aMovePos[][] =
            {
                    {ScreenConfig.getX(200), 0, 2},    // 시작점 남쪽으로
                    {ScreenConfig.getX(200), ScreenConfig.getY(500), 1}, // 우
                    {ScreenConfig.getX(800), ScreenConfig.getY(500), 4},    // 위
                    {ScreenConfig.getX(800), ScreenConfig.getY(200), 3},    // 좌
                    {ScreenConfig.getX(500), ScreenConfig.getY(200), 2},    // 밑

                    {ScreenConfig.getX(500), ScreenConfig.getY(1200), 1},    // 우
                    {ScreenConfig.getX(800), ScreenConfig.getY(1200), 4},    // 위
                    {ScreenConfig.getX(800), ScreenConfig.getY(900), 3},    // 좌
                    {ScreenConfig.getX(200), ScreenConfig.getY(900), 2},    // 밑 으로
                    {ScreenConfig.getX(200), ScreenConfig.getY(1300), 0}     // 종착지
            };

    // 다음 포지션으로 이동
    public void MovePosition() {
        // 이전 움직인 시간 이후 nSleep 이상 흘렀다면 움직인다.
        if ((System.currentTimeMillis() - lBeforTime) > nSleep) {
            lBeforTime = System.currentTimeMillis();
        } else {
            return;
        }

        // 방향 전환
        int nDir = aMovePos[nMoveArea - 1][2];

        if (nDirection != nDir) {
            nDirection = nDir;
        }

        if (nDirection == 0){
            GameState.nOverCount += 1;
            bUsed = false;
            nDirection = -1;
        }

        // 방향에 따라 몹이 이동
        if (nDir == 1) {
            nX += nSpeed;
            if (aMovePos[nMoveArea][0] < nX) nMoveArea++;
        } else if (
                nDir == 2) {
            nY += nSpeed;
            if (aMovePos[nMoveArea][1] < nY) nMoveArea++;
        } else if (nDir == 3) {
            nX -= nSpeed;
            if (aMovePos[nMoveArea][0] > nX) nMoveArea++;
        } else if (nDir == 4) {
            nY -= nSpeed;
            if (aMovePos[nMoveArea][1] > nY) nMoveArea++;
        }
    }
}
