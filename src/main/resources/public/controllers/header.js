function Header($scope, $http) {
    $http.get('/api/user')
        .success(function(data) {
            $scope.user= data;
            $scope.loggedIn = true;
            console.log("logged in");
        })
        .error(function(data){
            $scope.loggedIn = false;
            console.log("!logged in");
        })
    ;
}