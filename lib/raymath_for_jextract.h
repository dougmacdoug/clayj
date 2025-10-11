
Matrix MatrixLookAt(Vector3 eye, Vector3 target, Vector3 up);
Matrix MatrixIdentity(void);
Matrix MatrixPerspective(double fovY, double aspect, double nearPlane, double farPlane);
Matrix MatrixOrtho(double left, double right, double bottom, double top, double nearPlane, double farPlane);
Vector3 Vector3Unproject(Vector3 source, Matrix projection, Matrix view);
Vector3 Vector3Normalize(Vector3 v);
Vector3 Vector3Subtract(Vector3 v1, Vector3 v2);

