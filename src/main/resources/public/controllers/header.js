function Header($scope, $http) {
    $http.get('/band').
        success(function(data) {
            $scope.bands = data;
        });
}