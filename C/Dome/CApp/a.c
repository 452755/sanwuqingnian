#define _CRT_SECURE_NO_WARNINGS

#include <stdio.h>
#include <string.h>
//#include <datetimeapi.h>

#include "a.h"

typedef enum
{
	true = 1, false = 0
}bool;


int main() {
	bool b = false;
	printf("%d\n",b);
	int a = test(1,2);
	printf("%d\n", a);
	char str[20];
	scanf("%s", &str);
	printf("%s\n",str);
	return 0;
}

//int main() 
//{
//	bool flag = true;
//	do
//	{
//		char* op;
//		scanf("%c", &op);
//		switch (op)
//		{
//		default:
//			break;
//		}
//	} while (flag);
//	return 0;
//}


//int main() 
//{
//	int a = 0, b = 0;
//	char d = 'a';
//	while (1 == 1)
//	{
//		setbuf(stdin, NULL);
//		printf("请输入加(+)减(-)乘(*)除(/)\n");
//		scanf("%c", &d);
//		if (d == '+')
//		{
//			printf("请输入第一个操作数\n");
//			scanf("%d", &a);
//			printf("请输入第二个操作数\n");
//			scanf("%d", &b);
//			printf("%d\n", (a + b));
//		}
//		else if (d == '-')
//		{
//			printf("请输入第一个操作数\n");
//			scanf("%d", &a);
//			printf("请输入第二个操作数\n");
//			scanf("%d", &b);
//			printf("%d\n", (a - b));
//		}
//		else if (d == '*')
//		{
//			printf("请输入第一个操作数\n");
//			scanf("%d", &a);
//			printf("请输入第二个操作数\n");
//			scanf("%d", &b);
//			printf("%d\n", (a * b));
//		}
//		else if (d == '/')
//		{
//			printf("请输入第一个操作数\n");
//			scanf("%d", &a);
//			printf("请输入第二个操作数\n");
//			scanf("%d", &b);
//			printf("%d\n", (a / b));
//		}
//		else
//		{
//			break;
//		}
//	}
//	return 0;
//}



//#define AGE 10
//
//enum Sex
//{
//	MALE,
//	FEMALE,
//	SECRET
//};

//struct date
//{
//	int year;
//	int month;
//	int day;
//	int hours;
//};

//struct ren
//{
//	char name[20];
//	int age;
//	//date birthday;
//};

//int main() 
//{
//	printf("%s\n","\137");
//	return 0;
//}

//int main() 
//{
//	/*char arr1[] = "abc";
//	char arr2[] = {'g','b','c', '0',0};
//	printf("%s\n", arr1);
//	printf("%s\n", arr2);*/
//
//	/*enum Sex s = SECRET;
//	printf("%d\n", s);
//	printf("%d\n", AGE);*/
//
//	/*int a = 8;
//	Output(a);*/
//

//
//	/*int a, b, c;
//	scanf("%d\n%d", &a, &b);
//	c = aaa(a, b);
//	printf("%d", c);*/
//	
//	/*char str2[4] = "wang";
//	while (1 == 1)
//	{
//		int abc = 0;
//		char str[20];
//		scanf("%s", &str);
//		if (str == str2)
//		{
//			break;
//		}
//		printf("%s\n", str);
//	}*/
//	return 0;
//}