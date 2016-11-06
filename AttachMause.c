#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <windows.h>

int APIENTRY WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance,
		   LPSTR lpCmdLine, int nCmdShow)
{
	char buf[256],str[256],date[256];
  FILE *fp;
	int x,y,X,Y,L=0,R=0,Lb,Rb,PC_MAX_X,PC_MAX_Y,D_MAX_X,D_MAX_Y,LEN=26,flag=0;
	double CO_X,CO_Y;

	PC_MAX_X=GetSystemMetrics(SM_CXSCREEN);
	PC_MAX_Y=GetSystemMetrics(SM_CYSCREEN);

	while(1)
	{
		fp = fopen ("teraterm.log", "r");

		if(fgets(buf,256,fp)!=NULL&&flag==0)
			{
				if(strlen(buf)>=LEN-4)
				{
				sscanf(buf,"%d,%d",&D_MAX_X,&D_MAX_Y);

				CO_X=(double)PC_MAX_X/D_MAX_Y;
				CO_Y=(double)PC_MAX_Y/D_MAX_X;
				flag=1;
				printf("D_MAX_X %d\nD_MAX_Y %d\nCO_X %.20lf\nCO_Y %.20lf\n",D_MAX_X,D_MAX_Y, CO_X,CO_Y);
				}
			}

	  while (fgets(buf,256,fp) != NULL)
			{
					strcpy(date,str);
					strcpy(str,buf);
			}

			//printf("%s",date);

			if(strlen(date)>=LEN-4)
				{
					sscanf(date,"%d,%d,%d,%d",&X,&Y,&L,&R);

					x=round(1280-Y*CO_X);
					y=round(X*CO_Y);

					SetCursorPos(x,y);

					if(L==1&&Lb==0)
						mouse_event(MOUSEEVENTF_LEFTDOWN,0,0,0,0);
					else if(L==0&&Lb==1)
						mouse_event(MOUSEEVENTF_LEFTUP,0,0,0,0);
					else if(R==1&&Rb==0)
						mouse_event(MOUSEEVENTF_RIGHTDOWN,0,0,0,0);
					else if(R==0&&Rb==1)
						mouse_event(MOUSEEVENTF_RIGHTUP,0,0,0,0);

					Lb=L;
					Rb=R;
				}

	fclose( fp );

	}

	return 0;
}
