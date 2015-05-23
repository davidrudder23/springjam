function Register($scope, $http) {
    $http.get('/api/band').
        success(function(data) {
            $scope.bands = data;
        });


    $scope.user = {};

    $scope.register = function() {
        console.log($scope.user);

        if ($scope.user.password != $scope.user.password2) {
            alert ("passwords don't match!")
            return;
        }
        $http.post("/api/noauth/registerUser", $scope.user);
    };

}